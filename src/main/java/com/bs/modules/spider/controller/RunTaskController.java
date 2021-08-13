package com.bs.modules.spider.controller;

import com.bs.modules.spider.pojo.vo.RunTaskVO;
import com.bs.modules.sys.domain.SysUser;
import com.github.pagehelper.PageInfo;
import com.bs.modules.spider.domain.RunTask;
import com.bs.common.tools.string.Convert;
import com.bs.common.web.base.BaseController;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.common.web.domain.response.Result;
import com.bs.common.web.domain.response.module.ResultTable;
import com.bs.common.tools.secure.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.bs.modules.spider.service.IRunTaskService;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 爬虫任务运行Controller
 *
 * @author xucl
 * @date 2021-08-05
 */
@RestController
@RequestMapping("/spider/runTask")
public class RunTaskController extends BaseController
{
    private String prefix = "spider/runTask";

    @Autowired
    private IRunTaskService runTaskService;

    @GetMapping("/main")
    @PreAuthorize("hasPermission('/spider/runTask/main','spider:runTask:main')")
    public ModelAndView main()
    {
        return jumpPage(prefix + "/main");
    }


    /**
     * 新增爬虫任务运行
     */
    @GetMapping("/add")
    @PreAuthorize("hasPermission('/spider/runTask/add','spider:runTask:add')")
    public ModelAndView add()
    {
        return jumpPage(prefix + "/add");
    }

    /**
     * 查询爬虫任务运行列表
     */
    @ResponseBody
    @GetMapping("/data")
    @PreAuthorize("hasPermission('/spider/runTask/data','spider:runTask:data')")
    public ResultTable list(@ModelAttribute RunTask runTask, PageDomain pageDomain)
    {
        PageInfo<RunTaskVO> pageInfo = runTaskService.selectRunTaskPage(runTask,pageDomain);
        return pageTable(pageInfo.getList(),pageInfo.getTotal());
    }

    /**
     * 新增保存爬虫任务运行
     */
    @ResponseBody
    @PostMapping("/save")
    @PreAuthorize("hasPermission('/spider/runTask/add','spider:runTask:add')")
    public Result save(@RequestBody RunTask runTask)
    {
        runTask.setCreateTime(new Date());
        return decide(runTaskService.insertRunTask(runTask));
    }

    /**
     * 修改爬虫任务运行
     */
    @GetMapping("/edit")
    @PreAuthorize("hasPermission('/spider/runTask/edit','spider:runTask:edit')")
    public ModelAndView edit(Long id, ModelMap mmap)
    {
        RunTask runTask = runTaskService.selectRunTaskById(id);
        mmap.put("runTask", runTask);
        return jumpPage(prefix + "/edit");
    }

    /**
     * 修改保存爬虫任务运行
     */
    @ResponseBody
    @PutMapping("/update")
    @PreAuthorize("hasPermission('/spider/runTask/edit','spider:runTask:edit')")
    public Result update(@RequestBody RunTask runTask)
    {
        runTask.setUpdateTime(new Date());
        return decide(runTaskService.updateRunTask(runTask));
    }

    /**
     * 删除爬虫任务运行
     */
    @ResponseBody
    @DeleteMapping( "/batchRemove")
    @PreAuthorize("hasPermission('/spider/runTask/remove','spider:runTask:remove')")
    public Result batchRemove(String ids)
    {
        return decide(runTaskService.deleteRunTaskByIds(Convert.toStrArray(ids)));
    }

    /**
     * 删除
     */
    @ResponseBody
    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasPermission('/spider/runTask/remove','spider:runTask:remove')")
    public Result remove(@PathVariable("id") Long id)
    {
        return decide(runTaskService.deleteRunTaskById(id));
    }
}
