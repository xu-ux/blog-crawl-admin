package com.bs.modules.spider.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.bs.common.web.base.BaseDomain;

import javax.persistence.*;
import java.util.Date;

/**
 * 主题专栏对象 t_topic
 * 
 * @author xucl
 * @date 2021-08-06
 */
@Data
@Table(name = "t_topic")
@Accessors(chain = true)
public class Topic {


    private static final long serialVersionUID = 1L;

    /** 主题id */
    @Id
    @Column(name = "topic_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private Integer topicId;

    /** 主题名称 */
    private String topicName;

    /** 地址 */
    private String originalUrl;

    /** 类型 */
    private Short originalType;

    /** 数量 */
    private Integer size;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

}
