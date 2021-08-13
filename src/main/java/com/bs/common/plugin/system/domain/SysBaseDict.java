package com.bs.common.plugin.system.domain;

import com.bs.common.web.base.BaseDomain;
import lombok.Data;

/**
 * Describe: 字典值领域模型
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Data
public class SysBaseDict extends BaseDomain {

    /**
     *  id 编号
     * */
    private String dataId;

    /**
     *  字典显示
     * */
    private String dataLabel;

    /**
     * 字典值
     * */
    private String dataValue;

    /**
     * 字典类型
     * */
    private String typeCode;

    /**
     * 是否为默认
     * */
    private String isDefault;

    /**
     * 是否启用
     * */
    private String enable;

}
