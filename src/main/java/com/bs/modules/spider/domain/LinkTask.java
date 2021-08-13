package com.bs.modules.spider.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@Table(name = "t_link_task")
public class LinkTask {

    @Id
    @Column(name = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private Long id;

    /**
     * 原始地址
     */
    @Column(name = "link_url")
    private String linkUrl;



    @Column(name = "link_title")
    private String linkTitle;

    /**
     * 来源类型
     */
    @Column(name = "original_type")
    private Short originalType;

    /**
     * 运行状态 0停止 1进行中 2异常 3完成(已执行完爬取任务)
     */
    @Column(name = "run_status")
    private Short runStatus;

    /**
     * 错误信息
     */
    @Column(name = "error_msg")
    private String errorMsg;

    /**
     * 文件地址
     */
    @Column(name = "file_key")
    private String fileKey;

    /**
     * 提交用户
     */
    @Column(name = "user_id")
    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

}