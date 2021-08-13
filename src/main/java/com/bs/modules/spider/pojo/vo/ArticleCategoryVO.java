package com.bs.modules.spider.pojo.vo;

import lombok.Data;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleCategoryVO
 * @Description
 * @date 2021/8/5
 */
@Data
public class ArticleCategoryVO {

    private String url;

    private String title;

    private String categoryUrl;

    private String categoryTitle;

    private Integer categoryId;
}
