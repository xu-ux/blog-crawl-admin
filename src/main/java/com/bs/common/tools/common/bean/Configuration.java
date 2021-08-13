package com.bs.common.tools.common.bean;

import lombok.Data;

/**
 * @descriptions: yml配置实体
 * @author: xucl
 * @date: 2021/8/12
 * @version: 1.0
 */
@Data
public class Configuration {

    private AliyunOss aliyunOss;

    private FilePath filePath;

    private AppSwitch appSwitch;

}
