package com.bs.modules.spider.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.bs.modules.spider.domain.LinkTask;
import tk.mybatis.mapper.MyMapper;

/**
 * 标签管理Mapper接口
 * 
 * @author xucl
 * @date 2021-08-05
 */
@Mapper
public interface LinkTaskMapper extends MyMapper<LinkTask> {
    /**
     * 查询标签管理
     * 
     * @param id 标签管理ID
     * @return 标签管理
     */
    LinkTask selectLinkTaskById(Integer id);

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
     * 删除标签管理
     * 
     * @param id 标签管理ID
     * @return 结果
     */
    int deleteLinkTaskById(Integer id);

    /**
     * 批量删除标签管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteLinkTaskByIds(String[] ids);

}
