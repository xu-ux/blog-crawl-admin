package com.bs.modules.spider.service;

import java.util.List;

import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.RunTaskVO;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.RunTask;

/**
 * 爬虫任务运行Service接口
 * 
 * @author xucl
 * @date 2021-08-05
 */
public interface IRunTaskService {


    /**
     * 查询可以执行爬取逻辑的任务
     * @return
     */
    List<RunTask> listCrawlerRunTask();


    /**
     * 查询结束的任务
     * @return
     */
    List<RunTask> listFinishRunTask();

    /**
     * 新增任务
     * @param urlInfo
     */
    RunTask saveRunTask(ArticleUrlInfo urlInfo, Short runStatus);


    /**
     * 更新任务
     * @param task
     */
    int updateRunTask(RunTask task);

    /**
     * 异步更新任务未运行中
     * @param taskId
     */
    void updateRunTasking(Long taskId);

    /**
     * 查询爬虫任务运行
     * 
     * @param id 爬虫任务运行ID
     * @return 爬虫任务运行
     */
    RunTask selectRunTaskById(Long id);


    /**
    * 查询爬虫任务运行
     * @param ${classsName} 爬虫任务运行
     * @param pageDomain
     * @return 爬虫任务运行 分页集合
     * */
    PageInfo<RunTaskVO> selectRunTaskPage(RunTask runTask, PageDomain pageDomain);

    /**
     * 查询爬虫任务运行列表
     * 
     * @param runTask 爬虫任务运行
     * @return 爬虫任务运行集合
     */
    List<RunTask> selectRunTaskList(RunTask runTask);

    /**
     * 新增爬虫任务运行
     * 
     * @param runTask 爬虫任务运行
     * @return 结果
     */
    int insertRunTask(RunTask runTask);

    /**
     * 批量删除爬虫任务运行
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteRunTaskByIds(String[] ids);

    /**
     * 删除爬虫任务运行信息
     * 
     * @param id 爬虫任务运行ID
     * @return 结果
     */
    int deleteRunTaskById(Long id);

}
