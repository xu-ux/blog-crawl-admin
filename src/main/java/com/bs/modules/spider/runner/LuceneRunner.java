package com.bs.modules.spider.runner;

import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.mapper.ArticleMapper;
import com.bs.modules.spider.mapper.lucene.LuceneArticleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @descriptions: 初始化Lucene配置
 * @author: xucl
 * @date: 2021/8/25
 * @version: 1.0
 */
@Component
@Order(1)
public class LuceneRunner implements ApplicationRunner {

    @Autowired
    private LuceneArticleDao luceneArticleDao;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Example example = Example.builder(Article.class)
                .select("articleId","tag","title","mdContent","digest",
                        "originalId","originalDate","originalAuthor","originalUrl",
                        "originalType").build();
        List<Article> list = articleMapper.selectByExample(example);
        luceneArticleDao.createArticleIndex(list);
    }
}
