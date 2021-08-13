package com.bs.modules.spider.controller;

import com.bs.common.tools.string.StringUtil;
import com.bs.modules.spider.pojo.vo.LinkTaskVO;
import com.bs.modules.sys.domain.SysUser;
import com.github.pagehelper.PageInfo;
import com.bs.modules.spider.domain.LinkTask;
import com.bs.common.tools.string.Convert;
import com.bs.common.web.base.BaseController;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.common.web.domain.response.Result;
import com.bs.common.web.domain.response.module.ResultTable;
import com.bs.common.tools.secure.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.bs.modules.spider.service.ILinkTaskService;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 标签管理Controller
 *
 * @author xucl
 * @date 2021-08-05
 */
@RestController
@RequestMapping("/spider/linkTask")
public class LinkTaskController extends BaseController {

    private String prefix = "spider/linkTask";

    @Autowired
    private ILinkTaskService linkTaskService;

    @GetMapping("/main")
    @PreAuthorize("hasPermission('/spider/linkTask/main','spider:linkTask:main')")
    public ModelAndView main() {
        return jumpPage(prefix + "/main");
    }
    /**
     * 新增标签管理
     */
    @GetMapping("/add")
    @PreAuthorize("hasPermission('/spider/linkTask/add','spider:linkTask:add')")
    public ModelAndView add() {
        return jumpPage(prefix + "/add");
    }


    @GetMapping("/addFile")
    @PreAuthorize("hasPermission('/spider/linkTask/add','spider:linkTask:add')")
    public ModelAndView addFile() {
        return jumpPage(prefix + "/addFile");
    }


    /**
     * 查询标签管理列表
     */
    @ResponseBody
    @GetMapping("/data")
    @PreAuthorize("hasPermission('/spider/linkTask/data','spider:linkTask:data')")
    public ResultTable list(@ModelAttribute LinkTask linkTask, PageDomain pageDomain) {
        PageInfo<LinkTaskVO> pageInfo = linkTaskService.selectLinkTaskPage(linkTask, pageDomain);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }


    /**
     * 新增保存标签管理
     */
    @ResponseBody
    @PostMapping("/save")
    @PreAuthorize("hasPermission('/spider/linkTask/add','spider:linkTask:add')")
    public Result save(@RequestBody LinkTask linkTask) {
        try {
            if (StringUtils.isAnyBlank(linkTask.getLinkTitle(),linkTask
            .getLinkTitle())){
                return Result.failure("请填写完整信息");
            }
            return decide(linkTaskService.insertLinkTask(linkTask));
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }


    /**
     * 解析上传谷歌浏览器的导出书签文件
     *
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping("/upload")
    @PreAuthorize("hasPermission('/spider/linkTask/add','spider:linkTask:add')")
    public Result upload(@RequestParam("file") MultipartFile file){
        try{
            linkTaskService.analysisChromeFile(file);
            return Result.success("上传成功");
        }catch (Exception e){
            return Result.failure("上传失败 error:"+e.getMessage());
        }
    }

    /**
     * 修改标签管理
     */
    @GetMapping("/edit")
    @PreAuthorize("hasPermission('/spider/linkTask/edit','spider:linkTask:edit')")
    public ModelAndView edit(Integer id, ModelMap mmap) {
        LinkTask linkTask = linkTaskService.selectLinkTaskById(id);
        mmap.put("linkTask", linkTask);
        return jumpPage(prefix + "/edit");
    }

    /**
     * 修改保存标签管理
     */
    @ResponseBody
    @PutMapping("/update")
    @PreAuthorize("hasPermission('/spider/linkTask/edit','spider:linkTask:edit')")
    public Result update(@RequestBody LinkTask linkTask) {
        linkTask.setUpdateTime(new Date());
        return decide(linkTaskService.updateLinkTask(linkTask));
    }

    /**
     * 删除标签管理
     */
    @ResponseBody
    @DeleteMapping("/batchRemove")
    @PreAuthorize("hasPermission('/spider/linkTask/remove','spider:linkTask:remove')")
    public Result batchRemove(String ids) {
        return decide(linkTaskService.deleteLinkTaskByIds(Convert.toStrArray(ids)));
    }


    /**
     * 删除标签管理
     */
    @ResponseBody
    @PostMapping("/batchCrawl")
    public Result batchCrawl(String ids) {
        try {
            return decide(linkTaskService.crawlLinkTaskByIds(ids));
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }


    /**
     * 删除
     */
    @ResponseBody
    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasPermission('/spider/linkTask/remove','spider:linkTask:remove')")
    public Result remove(@PathVariable("id") Integer id) {
        return decide(linkTaskService.deleteLinkTaskById(id));
    }
}
