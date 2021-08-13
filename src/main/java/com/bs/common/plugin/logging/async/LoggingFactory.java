package com.bs.common.plugin.logging.async;

import com.bs.common.plugin.system.domain.SysBaseLog;
import com.bs.common.plugin.system.service.SysContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * Describe: 日 志 异 步 工 厂
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Component
public class LoggingFactory {

    /**
     * 引 入 日 志 服 务
     * */
    @Resource
    private SysContext sysContext;

    /**
     * 执 行 日 志 入 库 操 作
     * */
    @Async
    public void record(SysBaseLog sysLog){
        sysContext.saveLog(sysLog);
    }
}
