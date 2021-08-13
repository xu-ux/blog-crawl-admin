package com.bs.modules.spider.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleImgInfo
 * @Description
 * @date 2021/8/2
 */
@Data
@Accessors(chain = true)
public class ArticleImgInfo {

    private Long imgId;

    private Long articleId;

    /**
     * 原图片链接
     */
    private String originalUrl;

    /**
     * 新图片链接
     */
    private String imgUrl;

    /**
     * 图片大小kb
     */
    private Double imgSize;

    /**
     * oss的key
     */
    private String imgKey;

    /**
     * 本地oss的key
     */
    private String localImgKey;
}
