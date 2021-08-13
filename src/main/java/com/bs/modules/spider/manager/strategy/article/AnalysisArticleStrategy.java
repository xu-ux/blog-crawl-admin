package com.bs.modules.spider.manager.strategy.article;

import com.bs.common.exception.base.BusinessException;
import com.bs.common.exception.base.CommonException;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.safety.Whitelist;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName AnalysisArticleStrategy
 * @Description 策略接口和基础依赖
 * @date 2021/7/30
 */
public interface AnalysisArticleStrategy {


    /**
     * 执行爬取文章逻辑
     *
     * @param urlInfo
     * @return
     */
    Article doOperation(ArticleUrlInfo urlInfo) throws CommonException;


    /**
     * 校验url信息是否全部正确
     * @param urlInfo
     */
    default void checkUrInfo(ArticleUrlInfo urlInfo) {
        if (StringUtils.isAnyBlank(urlInfo.getOriginalId(),
                urlInfo.getOriginalUrl(),
                urlInfo.getOriginalType().toString())){
            throw new BusinessException("校验url信息未通过");
        }
    }

    /**
     * 清除指定标签的内联样式
     * @param element
     * @return
     */
    default String cleanStyles(Element element){
        //return Jsoup.clean(element.html(), Whitelist.relaxed().removeAttributes("span", "style"));
        return Jsoup.clean(element.html(), Whitelist.relaxed().addAttributes("span", "style"));
    }


    default String cleanWeiXinStyles(Element element){
        return Jsoup.clean(element.html(), Safelist.relaxed()
                .addAttributes("img","src","alt"));
    }
}
