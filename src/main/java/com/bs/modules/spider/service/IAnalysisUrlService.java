package com.bs.modules.spider.service;

import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;

import java.util.Optional;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IAnalysisUrlService
 * @Description
 * @date 2021/7/30
 */
public interface IAnalysisUrlService {

    /**
     * 解析url信息
     *
     * @param url
     * @return
     */
    ArticleUrlInfo parseUrl(String url);


    /**
     * 解析url信息
     *
     * @param url
     * @return
     */
    Optional<ArticleUrlInfo> parseUrlSecond(String url);

}
