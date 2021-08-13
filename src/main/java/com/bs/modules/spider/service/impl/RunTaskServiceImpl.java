package com.bs.modules.spider.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.bs.modules.spider.enums.OriginalType;
import com.bs.modules.spider.enums.RunStatus;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.RunTaskVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bs.modules.spider.mapper.RunTaskMapper;
import com.bs.modules.spider.domain.RunTask;
import com.bs.modules.spider.service.IRunTaskService;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

/**
 * 爬虫任务运行Service业务层处理
 * 
 * @author xucl
 * @date 2021-08-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RunTaskServiceImpl implements IRunTaskService {

    @Autowired
    private RunTaskMapper runTaskMapper;


    /**
     * 查询可以执行爬取逻辑的任务
     *
     * @return
     */
    @Override
    public List<RunTask> listCrawlerRunTask() {
        Example example = Example.builder(RunTask.class)
                .where(WeekendSqls.<RunTask>custom()
                        .andIn(RunTask::getRunStatus, Arrays.asList(-1))).build();
        return runTaskMapper.selectByExample(example);
    }

    /**
     * 查询结束的任务
     *
     * @return
     */
    @Override
    public List<RunTask> listFinishRunTask() {
        Example example = Example.builder(RunTask.class)
                .where(WeekendSqls.<RunTask>custom()
                        .andIn(RunTask::getRunStatus, Arrays.asList(2,3))).build();
        return runTaskMapper.selectByExample(example);
    }

    /**
     * 新增任务
     *
     * @param urlInfo
     * @param runStatus
     */
    @Override
    public RunTask saveRunTask(ArticleUrlInfo urlInfo, Short runStatus) {
        RunTask runTask = new RunTask();
        BeanUtils.copyProperties(urlInfo,runTask);
        runTask.setRunStatus(runStatus);
        runTask.setCreateTime(new Date());
        runTaskMapper.insert(runTask);
        return runTask;
    }

    /**
     * 更新任务
     *
     * @param task
     */
    @Override
    public int updateRunTask(RunTask task) {
        return runTaskMapper.updateByPrimaryKeySelective(task);
    }


    /**
     * 更新任务为运行中
     *
     * @param taskId
     */
    @Async
    @Override
    public void updateRunTasking(Long taskId) {
        try {
            RunTask runTask = new RunTask().setId(taskId).setRunStatus((short)1);
            runTaskMapper.updateByPrimaryKeySelective(runTask);
        } catch (Exception e) {
            log.error("更新任务为运行中异常，taskId:{}",taskId);
        }
    }

    /**
     * 查询爬虫任务运行
     * 
     * @param id 爬虫任务运行ID
     * @return 爬虫任务运行
     */
    @Override
    public RunTask selectRunTaskById(Long id)
    {
        return runTaskMapper.selectRunTaskById(id);
    }

    /**
     * 查询爬虫任务运行列表
     * 
     * @param runTask 爬虫任务运行
     * @return 爬虫任务运行
     */
    @Override
    public List<RunTask> selectRunTaskList(RunTask runTask)
    {
        return runTaskMapper.selectRunTaskList(runTask);
    }

    /**
     * 查询爬虫任务运行
     * @param runTask 爬虫任务运行
     * @param pageDomain
     * @return 爬虫任务运行 分页集合
     * */
    @Override
    public PageInfo<RunTaskVO> selectRunTaskPage(RunTask runTask, PageDomain pageDomain)
    {
        Page<Object> page = PageHelper.startPage(pageDomain.getPage(), pageDomain.getLimit());
        page.getTotal();
        List<RunTask> data = runTaskMapper.selectRunTaskList(runTask);
        PageInfo<RunTask> pageInfo = new PageInfo<>(data);


        List<RunTaskVO> collect = data.stream().map(s -> {
            RunTaskVO vo = new RunTaskVO();
            BeanUtils.copyProperties(s, vo);
            vo.setRunStatus(RunStatus.ofId(s.getRunStatus()).getName());
            vo.setOriginalType(OriginalType.ofId(s.getOriginalType()).getName());
            return vo;
        }).collect(Collectors.toList());

        PageInfo<RunTaskVO> voPageInfo = new PageInfo<>(collect);
        voPageInfo.setTotal(pageInfo.getTotal());
        return voPageInfo;
    }

    /**
     * 新增爬虫任务运行
     * 
     * @param runTask 爬虫任务运行
     * @return 结果
     */

    @Override
    public int insertRunTask(RunTask runTask)
    {
        return runTaskMapper.insertRunTask(runTask);
    }


    /**
     * 删除爬虫任务运行对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteRunTaskByIds(String[] ids)
    {
        return runTaskMapper.deleteRunTaskByIds(ids);
    }

    /**
     * 删除爬虫任务运行信息
     * 
     * @param id 爬虫任务运行ID
     * @return 结果
     */
    @Override
    public int deleteRunTaskById(Long id)
    {
        return runTaskMapper.deleteRunTaskById(id);
    }
}
