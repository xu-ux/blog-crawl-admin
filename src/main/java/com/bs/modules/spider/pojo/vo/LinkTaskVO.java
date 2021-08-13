package com.bs.modules.spider.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName LinkTaskVO
 * @Description
 * @date 2021/8/6
 */
@Data
public class LinkTaskVO {

    private Long id;

    /**
     * 原始地址
     */
    private String linkUrl;


    private String linkTitle;

    /**
     * 来源类型
     */
    private String originalType;

    /**
     * 运行状态
     */
    private String runStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 文件地址
     */
    private String fileKey;

    /**
     * 提交用户
     */
    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
