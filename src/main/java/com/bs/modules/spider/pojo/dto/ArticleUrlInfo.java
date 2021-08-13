package com.bs.modules.spider.pojo.dto;

import com.bs.modules.spider.enums.SourceType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleUrlInfo
 * @Description
 * @date 2021/7/30
 */
@Data
@Accessors(chain = true)
public class ArticleUrlInfo {

    /**
     * 原始地址
     */
    private String originalUrl;

    /**
     * 类型
     */
    private Short originalType;


    /**
     * sourceType
     */
    private SourceType sourceType;

    /**
     * 原始id
     */
    private String originalId;
}
