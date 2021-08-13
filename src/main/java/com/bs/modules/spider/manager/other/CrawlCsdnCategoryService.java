package com.bs.modules.spider.manager.other;

import com.bs.modules.spider.domain.Topic;
import com.bs.modules.spider.enums.SourceType;
import com.bs.modules.spider.mapper.TopicMapper;
import com.bs.modules.spider.pojo.vo.ArticleCategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName CrawCsdnCategoryService
 * @Description
 * @date 2021/8/5
 */
@Service
@Slf4j
public class CrawlCsdnCategoryService {

    @Autowired
    private TopicMapper topicMapper;


    /**
     * 解析文章分类页面的地址信息
     *
     * @param categoryUrl
     * @return
     * @throws Exception
     */
    public List<ArticleCategoryVO> analysisArticleUrl(String categoryUrl) throws Exception {
        Document document = Jsoup.connect(categoryUrl).get();

        Element categoryTitle = document.getElementsByClass("column_title oneline").get(0);

        Element element = document.selectFirst("#column > ul");
        Elements aList = element.select("li > a");

        String categoryText = categoryTitle.text();
        Topic topic = new Topic().setCreateTime(new Date())
                .setTopicName(categoryText)
                .setOriginalUrl(categoryUrl)
                .setSize(aList.size());
        Optional<SourceType> match = SourceType.match(categoryUrl);
        if (match.isPresent()){
            topic.setOriginalType((short)match.get().getId());
        }
        topicMapper.insert(topic);

        if (CollectionUtils.isNotEmpty(aList)){
            List<ArticleCategoryVO> collect = aList.stream().map(a -> {
                ArticleCategoryVO vo = new ArticleCategoryVO();
                String href = a.attr("href");

                Element title = a.getElementsByClass("title").get(0);

                vo.setUrl(href);
                vo.setTitle(title.text());
                vo.setCategoryTitle(categoryText);
                vo.setCategoryId(topic.getTopicId());
                vo.setCategoryUrl(categoryUrl);
                return vo;
            }).collect(Collectors.toList());

            return collect;
        }
        return null;

    }
}
