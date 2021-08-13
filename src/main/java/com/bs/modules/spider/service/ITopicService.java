package com.bs.modules.spider.service;

import java.util.List;

import com.bs.modules.spider.pojo.vo.TopicDetailVO;
import com.bs.modules.spider.pojo.vo.TopicVO;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.Topic;

/**
 * 主题专栏Service接口
 * 
 * @author xucl
 * @date 2021-08-06
 */
public interface ITopicService 
{
    /**
     * 查询主题专栏
     * 
     * @param topicId 主题专栏ID
     * @return 主题专栏
     */
    Topic selectTopicById(Long topicId);


    /**
    * 查询主题专栏
     * @param ${classsName} 主题专栏
     * @param pageDomain
     * @return 主题专栏 分页集合
     * */
    PageInfo<TopicVO> selectTopicPage(Topic topic, PageDomain pageDomain);

    /**
     * 查询专栏对应的文章信息
     *
     * @param topic
     * @param pageDomain
     * @return
     */
    PageInfo<TopicDetailVO> selectTopicDetailPage(Topic topic, PageDomain pageDomain);

    /**
     * 查询主题专栏列表
     * 
     * @param topic 主题专栏
     * @return 主题专栏集合
     */
    List<Topic> selectTopicList(Topic topic);

    /**
     * 新增主题专栏
     * 
     * @param topic 主题专栏
     * @return 结果
     */
    int insertTopic(Topic topic);

    /**
     * 修改主题专栏
     * 
     * @param topic 主题专栏
     * @return 结果
     */
    int updateTopic(Topic topic);

    /**
     * 批量删除主题专栏
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteTopicByIds(String[] ids);

    /**
     * 删除主题专栏信息
     * 
     * @param topicId 主题专栏ID
     * @return 结果
     */
    int deleteTopicById(Long topicId);

}
