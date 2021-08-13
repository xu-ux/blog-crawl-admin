package com.bs.modules.spider.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleVO
 * @Description
 * @date 2021/8/5
 */
@Data
public class ArticleVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    /**
     * 标签,使用','拼接
     */
    private String tag;

    /**
     * 封面,使用“,”拼接
     */
    private String cover;

    /**
     * 标题
     */
    private String title;

    /**
     * 原始id
     */
    private String originalId;

    /**
     * 原始地址
     */
    private String originalUrl;

    /**
     * 类型
     */
    private String originalType;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 原文字内容
     */
    private String textContent;

    /**
     * 发布时间
     */
    private String originalDateStr;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date originalDate;

    /**
     * 作者
     */
    private String originalAuthor;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
}
