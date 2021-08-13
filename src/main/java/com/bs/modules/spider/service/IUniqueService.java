package com.bs.modules.spider.service;

import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IUniqueService
 * @Description
 * @date 2021/8/3
 */
public interface IUniqueService {

    /**
     * 是否已经存在该原始地址，不存在则新增
     *
     * @param articleUrlInfo
     * @return
     */
    boolean existArticleUrlInfo(ArticleUrlInfo articleUrlInfo);

    /**
     * 保存唯一值
     * @param articleUrlInfo
     */
    void saveArticleUrlInfo(ArticleUrlInfo articleUrlInfo);

    /**
     * 删除唯一文章信息
     * @param originalId
     */
    void deleteArticleUnique(String originalId);
}
