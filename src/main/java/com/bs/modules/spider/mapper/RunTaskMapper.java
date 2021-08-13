package com.bs.modules.spider.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.bs.modules.spider.domain.RunTask;
import tk.mybatis.mapper.MyMapper;

/**
 * 爬虫任务运行Mapper接口
 * 
 * @author xucl
 * @date 2021-08-05
 */
@Mapper
public interface RunTaskMapper extends MyMapper<RunTask> {
    /**
     * 查询爬虫任务运行
     * 
     * @param id 爬虫任务运行ID
     * @return 爬虫任务运行
     */
    public RunTask selectRunTaskById(Long id);

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
     * 修改爬虫任务运行
     * 
     * @param runTask 爬虫任务运行
     * @return 结果
     */
    int updateRunTask(RunTask runTask);

    /**
     * 删除爬虫任务运行
     * 
     * @param id 爬虫任务运行ID
     * @return 结果
     */
    int deleteRunTaskById(Long id);

    /**
     * 批量删除爬虫任务运行
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteRunTaskByIds(String[] ids);

}
