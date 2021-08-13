package com.bs.modules.spider.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @descriptions: 主题专栏
 * @author: xucl
 * @date: 2021/8/10
 * @version: 1.0
 */
@Data
public class TopicVO {

    private Integer topicId;

    /** 主题名称 */
    private String topicName;

    /** 地址 */
    private String originalUrl;

    /** 类型 */
    private String originalType;

    /** 数量 */
    private Integer size;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
