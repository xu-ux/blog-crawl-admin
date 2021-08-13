package com.bs.modules.spider.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;


@Data
@Accessors(chain = true)
@Table(name = "t_article_img")
public class ArticleImg {


    @Id
    @Column(name = "img_id")
    private Long imgId;

    @Column(name = "article_id")
    private Long articleId;

    /**
     * 原图片链接
     */
    @Column(name = "original_url")
    private String originalUrl;

    /**
     * 新图片链接
     */
    @Column(name = "img_url")
    private String imgUrl;

    /**
     * 图片大小kb
     */
    @Column(name = "img_size")
    private Double imgSize;

    /**
     * oss的key
     */
    @Column(name = "img_key")
    private String imgKey;

    /**
     * 本地oss的key
     */
    @Column(name = "local_img_key")
    private String localImgKey;

}