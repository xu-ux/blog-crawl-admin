package com.bs.common.secure.process;

import com.alibaba.fastjson.JSON;
import com.bs.modules.sys.domain.SysLog;
import com.bs.common.plugin.logging.aop.enums.BusinessType;
import com.bs.common.plugin.logging.aop.enums.LoggingType;
import com.bs.modules.sys.service.ISysLogService;
import com.bs.common.tools.secure.SecurityUtil;
import com.bs.common.tools.sequence.SequenceUtil;
import com.bs.common.tools.servlet.ServletUtil;
import com.bs.common.web.domain.response.Result;
import com.bs.modules.sys.domain.SysUser;
import com.bs.modules.sys.service.ISysUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Describe: 自定义 Security 用户未登陆处理类
 * Author: test-admin
 * CreateTime: 2019/10/23
 */
@Component
public class SecureAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private ISysLogService sysLogService;

    @Resource
    private ISysUserService sysUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        SysLog sysLog = new SysLog();
        sysLog.setId(SequenceUtil.makeStringId());
        sysLog.setTitle("登录");
        sysLog.setDescription("登录成功");
        sysLog.setBusinessType(BusinessType.OTHER);
        sysLog.setSuccess(true);
        sysLog.setLoggingType(LoggingType.LOGIN);
        sysLogService.save(sysLog);

        SysUser sysUser = new SysUser();
        sysUser.setUserId(((SysUser) SecurityUtil.currentUser().getPrincipal()).getUserId());
        sysUser.setLastTime(LocalDateTime.now());
        sysUserService.update(sysUser);

        SysUser currentUser = (SysUser) authentication.getPrincipal();
        currentUser.setLastTime(LocalDateTime.now());
        request.getSession().setAttribute("currentUser", authentication.getPrincipal());
        Result result = Result.success("登录成功");
        ServletUtil.write(JSON.toJSONString(result));
    }
}
