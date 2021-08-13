package com.bs.modules.spider.service.impl;

import com.bs.modules.spider.domain.HistoryTask;
import com.bs.modules.spider.domain.RunTask;
import com.bs.modules.spider.mapper.HistoryTaskMapper;
import com.bs.modules.spider.mapper.RunTaskMapper;
import com.bs.modules.spider.service.IHistoryTaskService;
import com.bs.modules.spider.service.IRunTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName HistoryTaskServiceImpl
 * @Description
 * @date 2021/8/3
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class HistoryTaskServiceImpl implements IHistoryTaskService {


    @Autowired
    private IRunTaskService runTaskService;

    @Autowired
    private RunTaskMapper runTaskMapper;

    @Autowired
    private HistoryTaskMapper historyTaskMapper;

    /**
     * 转存已完成或者异常的任务到历史任务中
     */
    @Override
    public void saveHistoryTask() {
        List<RunTask> runTasks = runTaskService.listFinishRunTask();
        if (CollectionUtils.isNotEmpty(runTasks)){
            List<HistoryTask> collect = runTasks.stream().map(s -> {
                HistoryTask historyTask = new HistoryTask();
                BeanUtils.copyProperties(s, historyTask);
                return historyTask;
            }).collect(Collectors.toList());
            historyTaskMapper.insertList(collect);

            String s = runTasks.stream().map(RunTask::getId).map(String::valueOf).collect(Collectors.joining(","));
            runTaskMapper.deleteByIds(s);
        }

    }
}
