package com.bs.modules.spider.service;

import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.pojo.vo.ArticleVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IArticleService
 * @Description
 * @date 2021/8/2
 */
public interface IArticleService {


    /**
     * 新增文章
     * @param article
     */
    void saveArticle(Article article);

    /**
     * 查询文章
     *
     * @param articleId 文章ID
     * @return 文章
     */
    Article selectArticleById(Long articleId);


    /**
     * 查询文章
     * @param ${classsName} 文章
     * @param pageDomain
     * @return 文章 分页集合
     * */
    PageInfo<ArticleVO> selectArticlePage(Article article, PageDomain pageDomain);

    /**
     * 修改文章
     *
     * @param article 文章
     * @return 结果
     */
    int updateArticle(Article article);

    /**
     * 删除文章信息
     *
     * @param articleId 文章ID
     * @return 结果
     */
    int deleteArticleById(Long articleId);

}
