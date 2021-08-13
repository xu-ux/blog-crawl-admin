package com.bs.modules.sys.service.impl;

import com.bs.modules.sys.mapper.SysLogMapper;
import com.bs.modules.sys.domain.SysLog;
import com.bs.common.plugin.logging.aop.enums.LoggingType;
import com.bs.common.plugin.logging.aop.enums.RequestMethod;
import com.bs.modules.sys.service.ISysLogService;
import com.bs.common.tools.secure.SecurityUtil;
import com.bs.common.tools.servlet.ServletUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Describe: 日 志 服 务 接 口 实 现
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Service
public class SysLogServiceImpl implements ISysLogService {

    @Resource
    private SysLogMapper sysLogMapper;

    @Override
    public boolean save(SysLog sysLog) {
        sysLog.setOperateAddress(ServletUtil.getRemoteHost());
        sysLog.setMethod(ServletUtil.getRequestURI());
        sysLog.setCreateTime(LocalDateTime.now());
        sysLog.setRequestMethod(RequestMethod.valueOf(ServletUtil.getMethod()));
        sysLog.setOperateUrl(ServletUtil.getRequestURI());
        sysLog.setBrowser(ServletUtil.getBrowser());
        sysLog.setRequestBody(ServletUtil.getQueryParam());
        sysLog.setSystemOs(ServletUtil.getSystem());
        sysLog.setOperateName(null != SecurityUtil.currentUser() ? SecurityUtil.currentUser().getName() : "未登录用户");
        int result = sysLogMapper.insert(sysLog);
        return result > 0;
    }

    @Override
    public List<SysLog> data(LoggingType loggingType,LocalDateTime startTime,LocalDateTime endTime) {
        return sysLogMapper.selectList(loggingType,startTime,endTime);
    }

    @Override
    public SysLog getById(String id) {
        return sysLogMapper.getById(id);
    }

    @Override
    public List<SysLog> selectTopLoginLog(String operateName) {
        return sysLogMapper.selectTopLoginLog(operateName);
    }

}
