package com.bs.modules.spider.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleInfoVO
 * @Description
 * @date 2021/8/4
 */
@Data
public class ArticleInfoVO {

    @NotNull(message = "文章id不能为空")
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
     * 作者
     */
    private String originalAuthor;

}
