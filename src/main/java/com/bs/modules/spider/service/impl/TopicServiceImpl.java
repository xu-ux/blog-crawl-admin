package com.bs.modules.spider.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.enums.OriginalType;
import com.bs.modules.spider.enums.SourceType;
import com.bs.modules.spider.mapper.ArticleMapper;
import com.bs.modules.spider.pojo.vo.TopicDetailVO;
import com.bs.modules.spider.pojo.vo.TopicVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bs.modules.spider.mapper.TopicMapper;
import com.bs.modules.spider.domain.Topic;
import com.bs.modules.spider.service.ITopicService;

/**
 * 主题专栏Service业务层处理
 *
 * @author xucl
 * @date 2021-08-06
 */
@Slf4j
@Service
public class TopicServiceImpl implements ITopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 查询主题专栏
     *
     * @param topicId 主题专栏ID
     * @return 主题专栏
     */
    @Override
    public Topic selectTopicById(Long topicId) {
        return topicMapper.selectTopicById(topicId);
    }

    /**
     * 查询主题专栏列表
     *
     * @param topic 主题专栏
     * @return 主题专栏
     */
    @Override
    public List<Topic> selectTopicList(Topic topic) {
        return topicMapper.selectTopicList(topic);
    }

    /**
     * 查询主题专栏
     *
     * @param topic      主题专栏
     * @param pageDomain
     * @return 主题专栏 分页集合
     */
    @Override
    public PageInfo<TopicVO> selectTopicPage(Topic topic, PageDomain pageDomain) {
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getLimit());
        List<Topic> data = topicMapper.selectTopicList(topic);
        PageInfo<Topic> pageInfo = new PageInfo<>(data);


        List<TopicVO> collect = data.stream().map(s -> {
            TopicVO vo = new TopicVO();
            BeanUtils.copyProperties(s, vo);
            vo.setOriginalType(OriginalType.ofId(s.getOriginalType()).getName());
            return vo;
        }).collect(Collectors.toList());
        PageInfo<TopicVO> voPageInfo = new PageInfo<>(collect);
        voPageInfo.setTotal(pageInfo.getTotal());
        return voPageInfo;
    }

    /**
     * 查询专栏对应的文章信息
     *
     * @param topic
     * @param pageDomain
     * @return
     */
    @Override
    public PageInfo<TopicDetailVO> selectTopicDetailPage(Topic topic, PageDomain pageDomain) {

        Article article = new Article().setTopicId(topic.getTopicId());
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getLimit());
        List<Article> articles = articleMapper.selectArticleListByTopic(article);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);


        List<TopicDetailVO> collect = articles.stream().map(s -> {
            TopicDetailVO detailVO = new TopicDetailVO();
            BeanUtils.copyProperties(s, detailVO);
            return detailVO;
        }).collect(Collectors.toList());

        PageInfo<TopicDetailVO> voPageInfo = new PageInfo<>(collect);
        voPageInfo.setTotal(pageInfo.getTotal());
        return voPageInfo;
    }

    /**
     * 新增主题专栏
     *
     * @param topic 主题专栏
     * @return 结果
     */

    @Override
    public int insertTopic(Topic topic) {
        topic.setCreateTime(new Date());
        return topicMapper.insertTopic(topic);
    }

    /**
     * 修改主题专栏
     *
     * @param topic 主题专栏
     * @return 结果
     */
    @Override
    public int updateTopic(Topic topic) {
        topic.setUpdateTime(new Date());
        return topicMapper.updateTopic(topic);
    }

    /**
     * 删除主题专栏对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteTopicByIds(String[] ids) {
        return topicMapper.deleteTopicByIds(ids);
    }

    /**
     * 删除主题专栏信息
     *
     * @param topicId 主题专栏ID
     * @return 结果
     */
    @Override
    public int deleteTopicById(Long topicId) {
        return topicMapper.deleteTopicById(topicId);
    }
}
