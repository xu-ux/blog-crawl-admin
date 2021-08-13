package com.bs.modules.job.handler.base;

/**
 * Describe: 定时任务服务接口
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
public interface BaseTaskService {

    /**
     * 任 务 实 现
     * */
    void run(String params) throws Exception;
}
