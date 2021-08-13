package com.bs.modules.spider.service;

import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.domain.LinkTask;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.ArticleCategoryVO;

import java.util.List;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IAnalysisArticleService
 * @Description
 * @date 2021/7/30
 */
public interface IAnalysisArticleService {

    /**
     * 爬取文章
     * @param url
     * @throws Exception
     */
    void crawlerArticle(String url);


    void crawlerArticle(String url,Integer topicId);


    /**
     * 爬取文章
     * @throws Exception
     */
    void crawlerArticle(LinkTask linkTask);


    /**
     * 扫描任务中的url，进行爬取操作
     */
    void scanCrawlerTask();

    /**
     * 获取文章分类的所有文章
     * @param categoryUrl
     * @return
     */
    List<ArticleCategoryVO> crawlerArticleCategory(String categoryUrl);
}
