package com.bs.modules.spider.pojo.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleMdVO
 * @Description
 * @date 2021/8/4
 */
@Data
public class ArticleMdVO {

    @NotNull(message = "文章id不能为空")
    private Long articleId;

    /**
     * 已处理图片的md
     */
    @NotBlank(message = "MD内容不能为空")
    private String mdContent;


}
