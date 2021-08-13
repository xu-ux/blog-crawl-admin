package com.bs.modules.spider.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName RunTaskVO
 * @Description
 * @date 2021/8/5
 */
@Data
public class RunTaskVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 原始地址
     */
    private String originalUrl;

    /**
     * 来源类型
     */
    private String originalType;

    /**
     * 运行状态 -1待开始 0停止 1进行中 2异常 3完成(进入history)
     */
    private String runStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 提交用户
     */
    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
