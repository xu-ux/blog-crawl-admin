package com.bs.common.tools.common;

import com.bs.common.tools.common.bean.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * @descriptions: static下获取yml配置
 * @author: xucl
 * @date: 2021/8/12
 * @version: 1.0
 */
@Slf4j
public class ReadYmlUtils {


    private static String path = "/common-config.yml";


    public static String getValue(String key){
        Yaml yaml = new Yaml();
        try {
            InputStream in = ReadYmlUtils.class.getResourceAsStream(path);
            Map<String, Object> obj = yaml.load(in);
            return String.valueOf(obj.get(key));
        } catch (Exception e){
            log.error("读取文件{}，异常 error:{}",path,e.getMessage());
            throw new RuntimeException("读取文件异常");
        }
    }


    public static Configuration getConfig(){
        Yaml yaml = new Yaml();
        try {
            InputStream in = ReadYmlUtils.class.getResourceAsStream(path);
            return yaml.loadAs(in, Configuration.class );
        } catch (Exception e){
            log.error("读取文件{}，异常 error:{}",path,e.getMessage());
            throw new RuntimeException("读取文件异常");
        }
    }
}
