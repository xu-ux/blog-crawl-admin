package com.bs.modules.spider.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "t_run_task")
public class RunTask {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 原始地址
     */
    @Column(name = "original_url")
    private String originalUrl;

    /**
     * 来源类型
     */
    @Column(name = "original_type")
    private Short originalType;

    /**
     * 运行状态 -1待开始 0停止 1进行中 2异常 3完成(进入history)
     */
    @Column(name = "run_status")
    private Short runStatus;

    /**
     * 错误信息
     */
    @Column(name = "error_msg")
    private String errorMsg;

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