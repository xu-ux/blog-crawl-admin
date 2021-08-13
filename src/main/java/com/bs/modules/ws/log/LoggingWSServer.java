package com.bs.modules.ws.log;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName LoggingWSServer
 * @Description 日志实时打印WebSocket服务端
 * @date 2021/8/3
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/logging")
public class LoggingWSServer {

    /**
     * 连接集合
     */
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>(3);
    private static Map<String, Integer> lengthMap = new ConcurrentHashMap<>(3);

    /**
     * 匹配日期开头加换行，2019-08-12 14:15:04
     */
    private Pattern datePattern = Pattern.compile("[\\d+][\\d+][\\d+][\\d+]-[\\d+][\\d+]-[\\d+][\\d+] [\\d+][\\d+]:[\\d+][\\d+]:[\\d+][\\d+]");


    private static  ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        //添加到集合中
        sessionMap.put(session.getId(), session);
        //默认从第一行开始
        lengthMap.put(session.getId(), 1);
        //获取日志信息
        executorService.submit(() -> {
            log.info("LoggingWebSocketServer 任务开始");
            boolean first = true;
            BufferedReader reader = null;
            FileReader fileReader = null;
            while (sessionMap.get(session.getId()) != null) {
                try {
                    //日志文件，获取最新的

                    File path = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath()) ;
                    String fileName = path.getParentFile().getParent() + File.separator + "logs\\crawler-dev.log";
                    fileReader = new FileReader(fileName);
                    //字符流
                    reader = new BufferedReader(fileReader);
                    Object[] lines = reader.lines().toArray();

                    //只取从上次之后产生的日志
                    Object[] copyOfRange = Arrays.copyOfRange(lines, lengthMap.get(session.getId()), lines.length);

                    //对日志进行着色，更加美观  PS：注意，这里要根据日志生成规则来操作
                    for (int i = 0; i < copyOfRange.length; i++) {
                        String line = String.valueOf(copyOfRange[i]);
                        //先转义
                        line = line.replaceAll("&", "&amp;")
                                .replaceAll("<", "&lt;")
                                .replaceAll(">", "&gt;")
                                .replaceAll("\"", "&quot;");

                        //处理等级
                        line = line.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
                        line = line.replace("INFO", "<span style='color: green;'>INFO</span>");
                        line = line.replace("WARN", "<span style='color: orange;'>WARN</span>");
                        line = line.replace("ERROR", "<span style='color: red;'>ERROR</span>");

                        // 匹配日期开头加换行，2019-08-12 14:15:04
                        Matcher m = datePattern.matcher(line);
                        if (m.find( )) {
                            //找到下标
                            int start = m.start();
                            //插入
                            StringBuilder  sb = new StringBuilder (line);
                            sb.insert(start,"<br/>");
                            line = sb.toString();
                        }

                        copyOfRange[i] = line;
                    }

                    //存储最新一行开始
                    lengthMap.replace(session.getId(), lines.length);

                    //第一次如果太大，截取最新的200行就够了，避免传输的数据太大
                    if(first && copyOfRange.length > 200){
                        copyOfRange = Arrays.copyOfRange(copyOfRange, copyOfRange.length - 200, copyOfRange.length);
                        first = false;
                    }

                    String result = StringUtils.join(copyOfRange, "<br/>");

                    //发送
                    send(session, result);

                    //休眠一秒
                    Thread.sleep(1000);
                } catch (Exception e) {
                    //输出到日志文件中
                    log.error("获取日志信息异常",e);
                }
            }
            try {
                reader.close();
                fileReader.close();
            } catch (IOException e) {
                //输出到日志文件中
                log.error("获取日志信息关闭流异常",e);
            }
            log.info("LoggingWebSocketServer 任务结束");
        });
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        //从集合中删除
        sessionMap.remove(session.getId());
        lengthMap.remove(session.getId());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        //输出到日志文件中
        log.error("webSocket异常",error);
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端消息",message);
    }

    /**
     * 封装一个send方法，发送消息到前端
     */
    public void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            //输出到日志文件中
            log.error("发送消息异常 session:{}",session.getId(),e);
        }
    }
}
