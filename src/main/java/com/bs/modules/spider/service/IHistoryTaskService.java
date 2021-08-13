package com.bs.modules.spider.service;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName IHistoryTaskService
 * @Description
 * @date 2021/8/3
 */
public interface IHistoryTaskService {


    /**
     * 转存已完成或者异常的任务到历史任务中
     */
    void saveHistoryTask();
}
