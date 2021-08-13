package com.bs.modules.spider.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.bs.modules.spider.domain.Topic;
import tk.mybatis.mapper.MyMapper;

/**
 * 主题专栏Mapper接口
 * 
 * @author xucl
 * @date 2021-08-06
 */
@Mapper
public interface TopicMapper extends MyMapper<Topic> {
    /**
     * 查询主题专栏
     * 
     * @param topicId 主题专栏ID
     * @return 主题专栏
     */
    Topic selectTopicById(Long topicId);

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
     * 删除主题专栏
     * 
     * @param topicId 主题专栏ID
     * @return 结果
     */
    int deleteTopicById(Long topicId);

    /**
     * 批量删除主题专栏
     * 
     * @param topicIds 需要删除的数据ID
     * @return 结果
     */
    int deleteTopicByIds(String[] topicIds);

}
