package com.bs.common.config;

import com.bs.common.config.proprety.LocalProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName WebMvcConfig
 * @Description
 * @date 2021/8/4
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LocalProperty localProperty;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // addResourceLocations指的是文件放置的目录，addResourceHandler指的是对外暴露的访问路径
        registry.addResourceHandler("/articleFile/**")
                .addResourceLocations("file:"+localProperty.getRealPath());
    }

}