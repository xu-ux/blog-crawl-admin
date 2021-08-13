package com.bs.modules.spider.controller;

import com.bs.modules.spider.pojo.vo.TopicDetailVO;
import com.bs.modules.spider.pojo.vo.TopicVO;
import com.github.pagehelper.PageInfo;
import com.bs.modules.spider.domain.Topic;
import com.bs.common.tools.string.Convert;
import com.bs.common.web.base.BaseController;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.common.web.domain.response.Result;
import com.bs.common.web.domain.response.module.ResultTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.bs.modules.spider.service.ITopicService;

/**
 * 主题专栏Controller
 *
 * @author xucl
 * @date 2021-08-06
 */
@RestController
@RequestMapping("/spider/topic")
public class TopicController extends BaseController {
    private String prefix = "spider/topic";

    @Autowired
    private ITopicService topicService;

    @GetMapping("/main")
    @PreAuthorize("hasPermission('/spider/topic/main','spider:topic:main')")
    public ModelAndView main() {
        return jumpPage(prefix + "/main");
    }

    /**
     * 查询主题专栏列表
     */
    @ResponseBody
    @GetMapping("/data")
    @PreAuthorize("hasPermission('/spider/topic/data','spider:topic:data')")
    public ResultTable list(@ModelAttribute Topic topic, PageDomain pageDomain) {
        PageInfo<TopicVO> pageInfo = topicService.selectTopicPage(topic, pageDomain);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 查询主题专栏列表
     */
    @ResponseBody
    @GetMapping("/data/detail")
    @PreAuthorize("hasPermission('/spider/topic/data','spider:topic:data')")
    public ResultTable listDetail(@ModelAttribute Topic topic, PageDomain pageDomain) {
        PageInfo<TopicDetailVO> pageInfo = topicService.selectTopicDetailPage(topic, pageDomain);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 新增主题专栏
     */
    @GetMapping("/add")
    @PreAuthorize("hasPermission('/spider/topic/add','spider:topic:add')")
    public ModelAndView add() {
        return jumpPage(prefix + "/add");
    }

    /**
     * 新增保存主题专栏
     */
    @ResponseBody
    @PostMapping("/save")
    @PreAuthorize("hasPermission('/spider/topic/add','spider:topic:add')")
    public Result save(@RequestBody Topic topic) {
        return decide(topicService.insertTopic(topic));
    }

    /**
     * 修改主题专栏
     */
    @GetMapping("/edit")
    @PreAuthorize("hasPermission('/spider/topic/edit','spider:topic:edit')")
    public ModelAndView edit(Long topicId, ModelMap mmap) {
        Topic topic = topicService.selectTopicById(topicId);
        mmap.put("topic", topic);
        return jumpPage(prefix + "/edit");
    }

    /**
     * 修改保存主题专栏
     */
    @ResponseBody
    @PutMapping("/update")
    @PreAuthorize("hasPermission('/spider/topic/edit','spider:topic:edit')")
    public Result update(@RequestBody Topic topic) {
        return decide(topicService.updateTopic(topic));
    }

    /**
     * 删除主题专栏
     */
    @ResponseBody
    @DeleteMapping("/batchRemove")
    @PreAuthorize("hasPermission('/spider/topic/remove','spider:topic:remove')")
    public Result batchRemove(String ids) {
        return decide(topicService.deleteTopicByIds(Convert.toStrArray(ids)));
    }

    /**
     * 删除
     */
    @ResponseBody
    @DeleteMapping("/remove/{topicId}")
    @PreAuthorize("hasPermission('/spider/topic/remove','spider:topic:remove')")
    public Result remove(@PathVariable("topicId") Long topicId) {
        return decide(topicService.deleteTopicById(topicId));
    }
}
