package com.bs.modules.spider.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ImgPath
 * @Description
 * @date 2021/8/4
 */
@Data
@Accessors(chain = true)
public class ImgPath {

    private String oldUrl;

    private String ossUrl;

    private String ossKey;

    private Double fileSize;

    private String localKey;
}
