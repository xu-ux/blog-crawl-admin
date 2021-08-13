package com.bs.modules.spider.manager.strategy.article;

import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.manager.strategy.article.AnalysisArticleStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import org.springframework.stereotype.Component;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName SeleniumAnalysisArticle
 * @Description
 * @date 2021/7/30
 */
@Component
public class SeleniumAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {


    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) {
        return null;
    }

}
