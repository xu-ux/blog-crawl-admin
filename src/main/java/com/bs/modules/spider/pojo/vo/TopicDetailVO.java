package com.bs.modules.spider.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @descriptions: 详细文章信息
 * @author: xucl
 * @date: 2021/8/10
 * @version: 1.0
 */
@Data
@Accessors(chain = true)
public class TopicDetailVO {

    private Long articleId;

    private Integer topicId;

    /**
     * 标签,使用','拼接
     */
    private String tag;

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
     * 摘要
     */
    private String digest;
}
