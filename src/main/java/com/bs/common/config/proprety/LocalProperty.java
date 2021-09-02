package com.bs.common.config.proprety;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName LocalProperty
 * @Description 本地文件相对路径
 * @date 2021/8/4
 */
@Data
@Component
@ConfigurationProperties("blogsearch.local")
public class LocalProperty {

    private String basedir;

    private String mdFilePath;

    private String tagFilePath;

    private String driverWin;

    private String driverLinux;

    private String lucenePath;


    /**
     * md图片文件夹
     * @return
     */
    public String getRealPath(){
        File path = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath()) ;
        return path.getParentFile().getParent() + File.separator + this.basedir.concat(this.mdFilePath);
    }

    /**
     * 索引文件夹
     * @return
     */
    public String getLucenePath(){
        File path = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath()) ;
        return path.getParentFile().getParent() + File.separator + this.basedir.concat(this.lucenePath);
    }

    /**
     * html文件保存的文件夹
     * @return
     */
    public String getRealTagFilePath(){
        File path = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath()) ;
        return path.getParentFile().getParent() + File.separator + this.basedir.concat(this.tagFilePath);
    }



}
