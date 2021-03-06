package com.bs.common.secure.process;

import com.alibaba.fastjson.JSON;
import com.bs.common.tools.servlet.ServletUtil;
import com.bs.common.web.domain.response.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Describe: 自定义 Security 用户未登陆处理类
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Component
public class SecureAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Result result = Result.failure(401,"未知账户");
        ServletUtil.write(JSON.toJSONString(result));
    }
}
