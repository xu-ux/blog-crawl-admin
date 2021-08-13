package com.bs.modules.job.task;

import com.bs.modules.job.handler.base.BaseTaskService;
import com.bs.modules.spider.service.IHistoryTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName HistoryTaskImpl
 * @Description
 * @date 2021/8/3 10:06
 */
@Slf4j
@Component("historyTask")
public class HistoryTaskImpl implements BaseTaskService {

    @Autowired
    private IHistoryTaskService historyTaskService;
    /**
     * 任 务 实 现
     *
     * @param params
     */
    @Override
    public void run(String params) throws Exception {
        historyTaskService.saveHistoryTask();
    }
}
