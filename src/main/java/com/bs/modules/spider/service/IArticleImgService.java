package com.bs.modules.spider.service;

import com.bs.modules.spider.pojo.dto.ArticleImgInfo;
import com.bs.modules.spider.pojo.dto.ImgPath;

import java.util.List;
import java.util.Map;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IArticleImgService
 * @Description
 * @date 2021/8/2
 */
public interface IArticleImgService {

    /**
     * 转换图片为oss图片
     * @param html
     * @param articleId
     * @return
     */
    String convertImgWithHtml(String html,Long articleId);


    /**
     * 转换图片为oss图片
     * @param imgList
     * @param articleId
     * @return
     */
    String convertImgWithUrl(List<String> imgList,String contentHtml,Long articleId);

    /**
     * 转换图片为oss图片
     * @param md
     * @return
     */
    String convertImgWithMD(String md, Long articleId);



    String convertImgWithHtmlSecond(String contentHtml, Long articleId) throws Exception;


    /**
     * 保存图片信息
     *
     * @param infoList
     * @param articleId
     */
    void saveArticleImg(List<ArticleImgInfo> infoList, Long articleId);


    /**
     * 批量上传图片至本地和oss
     * @param imgUrl
     * @return
     */
    Map<String, ImgPath> uploadOssImgAndLocal(List<String> imgUrl,Long articleId) throws InterruptedException;


}
