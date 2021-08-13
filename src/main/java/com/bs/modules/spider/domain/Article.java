package com.bs.modules.spider.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * t_article
 * @author 
 */
@Data
@Accessors(chain = true)
@Table(name="t_article")
public class Article implements Serializable {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;


    private Integer topicId;

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
    private Short originalType;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 原文字内容
     */
    private String textContent;

    /**
     * 原html内容
     */
    private String originalHtmlContent;

    /**
     * 已处理图片的html内容
     */
    private String htmlContent;

    /**
     * 未处理图片的md
     */
    private String originalMdContent;

    /**
     * 已处理图片的md
     */
    private String mdContent;

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

    private static final long serialVersionUID = 1L;

}