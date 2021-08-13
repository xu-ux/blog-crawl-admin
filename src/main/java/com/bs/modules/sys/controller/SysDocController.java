package com.bs.modules.sys.controller;

import com.bs.common.constant.ControllerConstant;
import com.bs.common.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Describe: 接口文档控制器
 * Author: test-admin
 * CreateTime: 2019/10/23
 */
@RestController
@Api(tags = {"接口文档"})
@RequestMapping(ControllerConstant.API_SYSTEM_PREFIX + "doc")
public class SysDocController extends BaseController {

    private String MODULE_PATH = "system/doc/";

    @GetMapping("main")
    @PreAuthorize("hasPermission('/system/doc/main','sys:doc:main')")
    public ModelAndView main(){
        return jumpPage(MODULE_PATH + "main");
    }
}
