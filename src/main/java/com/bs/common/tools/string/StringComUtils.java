package com.bs.common.tools.string;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName StringComUtils
 * @Description 字符串公共处理工具
 * @date 2021/6/21 17:28
 */
public class StringComUtils {

    /**
     * 限制文本描述
     *
     * @param content 内容或问题
     * @param charNumber 长度
     * @return
     */
    public static String limitStr(String content ,int charNumber){
        if (StringUtils.isNotBlank(content)){
            if (content.length() > charNumber){
                String substring = content.substring(0, charNumber);
                return substring+"...";
            }else {
                return content;
            }
        }
        return "";
    }

    /**
     * 限制文本描述
     *
     * @param content 内容或问题
     * @param charNumber 长度
     * @return
     */
    public static String limitStrNone(String content ,int charNumber){
        if (StringUtils.isNotBlank(content)){
            if (content.length() > charNumber){
                String substring = content.substring(0, charNumber);
                return substring;
            }else {
                return content;
            }
        }
        return "";
    }

    /**
     * 过滤非UTF8字符
     *
     * @param text
     * @return
     */
    public static String filterOffUtf8Mb4(String text)  {
        try {
            if (text == null && text.length() == 0 ){
                return "";
            }
            byte[] bytes = text.getBytes("utf-8");
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            int i = 0;
            while (i < bytes.length) {
                short b = bytes[i];
                if (b > 0) {
                    buffer.put(bytes[i++]);
                    continue;
                }
                //去掉符号位
                b += 256;

                if (((b >> 5) ^ 0x06) == 0) {
                    buffer.put(bytes, i, 2);
                    i += 2;
                    System.out.println("2");
                } else if (((b >> 4) ^ 0x0E) == 0) {
                    System.out.println("3");
                    buffer.put(bytes, i, 3);
                    i += 3;
                } else if (((b >> 3) ^ 0x1E) == 0) {
                    i += 4;
                    System.out.println("4");
                } else if (((b >> 2) ^ 0xBE) == 0) {
                    i += 5;
                    System.out.println("5");
                } else {
                    i += 6;
                    System.out.println("6");
                }
            }
            buffer.flip();
            return new String(buffer.array(), "utf-8");
        } catch (Exception e) {
            return "";
        }
    }
}
