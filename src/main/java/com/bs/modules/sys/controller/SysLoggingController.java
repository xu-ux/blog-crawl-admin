package com.bs.modules.sys.controller;

import com.bs.common.constant.ControllerConstant;
import com.bs.common.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName SysLoggingController
 * @Description
 * @date 2021/8/3 14:04
 */
@RestController
@Api(tags = {"实时日志"})
@RequestMapping(ControllerConstant.API_SYSTEM_PREFIX + "monitor")
public class SysLoggingController extends BaseController {

    /**
     * 端口
     */
    @Value("${server.port}")
    private String port;

    /**
     * 跳转实时日志
     */
    @GetMapping("logging")
    public ModelAndView logging() {
        return jumpPage("system/monitor/logging",new HashMap<String,String>(){{
            put("port",port);
        }} );
    }
}
