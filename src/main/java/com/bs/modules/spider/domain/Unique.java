package com.bs.modules.spider.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "t_unique")
public class Unique {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private Long id;

    /**
     * 原始id
     */
    @Column(name = "original_id")
    private String originalId;

    /**
     * 原始地址
     */
    @Column(name = "original_url")
    private String originalUrl;

    /**
     * 类型
     */
    @Column(name = "original_type")
    private Short originalType;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

}