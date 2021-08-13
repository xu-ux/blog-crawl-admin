package com.bs.modules.sys.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * Describe: 用户角色映射关系
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */

@Data
@Alias("SysUserRole")
public class SysUserRole {

    /**
     * 映射标识
     * */
    private String id;

    /**
     * 用户编号
     * */
    private String userId;

    /**
     * 角色编号
     * */
    private String roleId;

}
