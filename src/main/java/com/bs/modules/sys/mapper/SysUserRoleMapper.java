package com.bs.modules.sys.mapper;

import com.bs.modules.sys.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * Describe: 用户角色接口
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Mapper
public interface SysUserRoleMapper {

    int batchInsert(List<SysUserRole> sysUserRoles);

    int deleteByUserId(String userId);

    int deleteByUserIds(String[] userIds);

    int deleteByRoleId(String roleId);

    int deleteByRoleIds(String[] roleIds);

    List<SysUserRole> selectByUserId(String userId);
}
