package com.bs.modules.spider.mapper.lucene;

import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.Article;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @descriptions: lucene索引操作层
 * @author: xucl
 * @date: 2021/8/25
 * @version: 1.0
 */
public interface ILuceneArticleDao {

    /**
     * 创建索引
     * @param articleList
     */
    void createArticleIndex(List<Article> articleList);

    /**
     * 查询文章
     * @param article 查询条件
     * @param pageDomain 分页参数
     * @return
     * @throws Exception
     */
    PageInfo<Article> searchProduct(Article article, PageDomain pageDomain) throws Exception;
}
