package com.bs.modules.spider.service.impl;

import com.bs.common.exception.base.BusinessException;
import com.bs.common.exception.base.CommonException;
import com.bs.modules.spider.enums.SourceType;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.service.IAnalysisUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName AnalysisUrlServiceImpl
 * @Description
 * @date 2021/7/30
 */
@Slf4j
@Service
public class AnalysisUrlServiceImpl implements IAnalysisUrlService {


    /**
     * 解析url信息
     *
     * @param url
     * @return
     */
    @Override
    public ArticleUrlInfo parseUrl(String url) throws CommonException {
        // 1. 通过主域名匹配url的源头
        Optional<SourceType> type = SourceType.match(url);
        if (!type.isPresent()) {
            log.error("无法匹配url地址 url:{}",url);
            throw new CommonException("无法匹配url地址 url:{}",url);
        }
        try {
            SourceType sourceType = type.get();
            Pattern pattern = Pattern.compile(sourceType.getRegStr());
            Matcher matcher = pattern.matcher(url);
            String id = "";
            if (matcher.find()){
                // 0是本身
                id = matcher.group(sourceType.getGroup()).trim();
            }

            return new ArticleUrlInfo().setOriginalUrl(url).setOriginalType((short)sourceType.getId())
                    .setOriginalId(id).setSourceType(sourceType);
        } catch (Exception e) {
            log.error("解析地址发生异常 url:{}",url,e);
            throw new CommonException("无法匹配url地址 url:{} msg:{}",url,e.getMessage());
        }
    }

    /**
     * 解析url信息
     *
     * @param url
     * @return
     */
    @Override
    public Optional<ArticleUrlInfo> parseUrlSecond(String url) {
        // 1. 通过主域名匹配url的源头
        Optional<SourceType> type = SourceType.match(url);
        if (!type.isPresent()) {
           return Optional.empty();
        }
        try {
            SourceType sourceType = type.get();
            Pattern pattern = Pattern.compile(sourceType.getRegStr());
            Matcher matcher = pattern.matcher(url);
            String id = "";
            String realUrl = "";
            if (matcher.find()){
                // 0是本身
                id = matcher.group(sourceType.getGroup()).trim();
                realUrl = matcher.group(0);
                log.debug("url:{} realUrl:{}",url,realUrl);
            }

            return Optional.of(new ArticleUrlInfo().setOriginalUrl(realUrl).setOriginalType((short)sourceType.getId())
                    .setOriginalId(id).setSourceType(sourceType));
        } catch (Exception e) {
            log.error("解析地址发生异常 url:{}",url,e);
            return Optional.empty();
        }
    }
}
