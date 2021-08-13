package com.bs.modules.spider.mapper;

import com.bs.modules.spider.domain.Article;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author admin
 */
@Mapper
public interface ArticleMapper extends MyMapper<Article> {


    List<Article> selectArticleList(Article article);


    List<Article> selectArticleListByTopic(Article article);


    int updateArticle(Article article);

}