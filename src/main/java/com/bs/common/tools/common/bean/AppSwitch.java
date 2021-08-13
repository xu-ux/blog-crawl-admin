package com.bs.common.tools.common.bean;

import lombok.Data;

/**
 * @descriptions: 开关配置
 * @author: xucl
 * @date: 2021/8/12
 * @version: 1.0
 */
@Data
public class AppSwitch {

    /**
     * 是否打开oss上传
     * true:上传  false:不上传
     */
    private boolean uploadOss;
}
