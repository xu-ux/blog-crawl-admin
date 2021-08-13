package com.bs.modules.spider.service;

import java.util.List;

import com.bs.modules.spider.pojo.vo.LinkTaskVO;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.LinkTask;
import org.springframework.web.multipart.MultipartFile;

/**
 * 标签管理Service接口
 * 
 * @author xucl
 * @date 2021-08-05
 */
public interface ILinkTaskService 
{
    /**
     * 查询标签管理
     * 
     * @param id 标签管理ID
     * @return 标签管理
     */
    LinkTask selectLinkTaskById(Integer id);


    /**
    * 查询标签管理
     * @param ${classsName} 标签管理
     * @param pageDomain
     * @return 标签管理 分页集合
     * */
    PageInfo<LinkTaskVO> selectLinkTaskPage(LinkTask linkTask, PageDomain pageDomain);

    /**
     * 查询标签管理列表
     * 
     * @param linkTask 标签管理
     * @return 标签管理集合
     */
    List<LinkTask> selectLinkTaskList(LinkTask linkTask);

    /**
     * 新增标签管理
     * 
     * @param linkTask 标签管理
     * @return 结果
     */
    int insertLinkTask(LinkTask linkTask);

    /**
     * 修改标签管理
     * 
     * @param linkTask 标签管理
     * @return 结果
     */
    int updateLinkTask(LinkTask linkTask);

    /**
     * 批量删除标签管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteLinkTaskByIds(String[] ids);

    /**
     * 删除标签管理信息
     * 
     * @param id 标签管理ID
     * @return 结果
     */
    int deleteLinkTaskById(Integer id);

    /**
     * 解析谷歌浏览前导出文件
     *
     * @param file
     */
    void analysisChromeFile(MultipartFile file);

    /**
     * 批量爬取链接地址
     * @param ids
     * @return
     */
    int crawlLinkTaskByIds(String ids);
}
