package com.bs.modules.spider.service.impl;

import com.bs.common.tools.common.BeanHelper;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.domain.ArticleImg;
import com.bs.modules.spider.enums.OriginalType;
import com.bs.modules.spider.mapper.ArticleImgMapper;
import com.bs.modules.spider.mapper.ArticleMapper;
import com.bs.modules.spider.mapper.lucene.ILuceneArticleDao;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.ArticleVO;
import com.bs.modules.spider.service.IAnalysisArticleService;
import com.bs.modules.spider.service.IAnalysisUrlService;
import com.bs.modules.spider.service.IArticleService;
import com.bs.modules.spider.service.IUniqueService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleServiceImpl
 * @Description
 * @date 2021/8/2
 */
@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ILuceneArticleDao luceneArticleDao;

    @Autowired
    private ArticleImgMapper articleImgMapper;

    @Autowired
    private IUniqueService uniqueService;

    /**
     * 新增文章
     *
     * @param article
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(Article article) {
        articleMapper.insert(article);
    }

    /**
     * 查询文章
     *
     * @param articleId 文章ID
     * @return 文章
     */
    @Override
    public Article selectArticleById(Long articleId) {
        return articleMapper.selectByPrimaryKey(articleId);
    }

    /**
     * 查询文章
     *
     * @param article
     * @param pageDomain
     * @return 文章 分页集合
     */
    @Override
    public PageInfo<ArticleVO> selectArticlePage(Article article, PageDomain pageDomain) throws Exception {
/*        PageHelper.startPage(pageDomain.getPage(), pageDomain.getLimit());

        List<Article> select = articleMapper.selectArticleList(article);
        PageInfo<Article> pageInfo = new PageInfo<>(select);

        List<ArticleVO> articleVOS = select.stream().map(s -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(s, vo);
            vo.setOriginalType(OriginalType.ofId(s.getOriginalType()).getName());
            return vo;
        }).collect(Collectors.toList());

        PageInfo<ArticleVO> voPageInfo = new PageInfo<>(articleVOS);
        voPageInfo.setTotal(pageInfo.getTotal());*/
        PageInfo<Article> pageInfo = luceneArticleDao.searchProduct(article, pageDomain);
        List<ArticleVO> articleVOS = pageInfo.getList().stream().map(s -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(s, vo);
            vo.setOriginalType(OriginalType.ofId(s.getOriginalType()).getName());
            return vo;
        }).collect(Collectors.toList());
        PageInfo<ArticleVO> voPageInfo = new PageInfo<>(articleVOS);
        voPageInfo.setTotal(pageInfo.getTotal());
        return voPageInfo;
    }

    /**
     * 修改文章
     *
     * @param article 文章
     * @return 结果
     */
    @Override
    public int updateArticle(Article article) {
        int i = articleMapper.updateArticle(article);
        return i ;
    }

    /**
     * 删除文章信息
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @Override
    public int deleteArticleById(Long articleId) {
        Example sel = Example.builder(Article.class).select("originalId")
                .where(Sqls.custom().andEqualTo("articleId",articleId)).build();
        Article article = articleMapper.selectOneByExample(sel);

        int rowsArticle = articleMapper.deleteByPrimaryKey(articleId);
        log.info("已删除 articleId:{} rows:{}",articleId,rowsArticle);

        Example example = Example.builder(ArticleImg.class)
                .where(WeekendSqls.<ArticleImg>custom().andEqualTo(ArticleImg::getArticleId,articleId)).build();
        int rowsImg = articleImgMapper.deleteByExample(example);
        log.info("已删除关联图片 articleId:{} rows:{}",articleId,rowsImg);

        uniqueService.deleteArticleUnique(article.getOriginalId());
        return rowsArticle;
    }

}
