package com.bs.modules.sys.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * Describe: 角色权限映射关系
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Data
@Alias("SysRolePower")
public class SysRolePower {

    /**
     * 映射编号
     * */
    private String id;

    /**
     * 角色编号
     * */
    private String roleId;

    /**
     * 权限编号
     * */
    private String powerId;

}
