package com.bs.common.secure.process;

import com.alibaba.fastjson.JSON;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bs.common.tools.servlet.ServletUtil;
import com.bs.common.web.domain.response.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Describe: 自定义 Security 用户暂无权限处理类
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Component
public class SecureAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        if(ServletUtil.isAjax(httpServletRequest)){
            Result result = Result.failure(403,"暂无权限");
            ServletUtil.write(JSON.toJSONString(result));
        }else{
            httpServletResponse.sendRedirect("/error/403");
        }
    }
}
