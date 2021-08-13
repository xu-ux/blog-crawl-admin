package com.bs.modules.spider.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.bs.common.config.MyThreadPool;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.domain.LinkTask;
import com.bs.modules.spider.domain.RunTask;

import com.bs.modules.spider.manager.other.CrawlCsdnCategoryService;
import com.bs.modules.spider.manager.strategy.article.AnalysisArticleContext;
import com.bs.modules.spider.mapper.LinkTaskMapper;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.ArticleCategoryVO;
import com.bs.modules.spider.service.*;
import com.bs.modules.spider.util.ArticleUtils;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Author xucl
 * @Version 1.0
 * @ClassName AnalysisArticleServiceImpl
 * @Description
 * @date 2021/7/30
 */
@Slf4j
@Service
public class AnalysisArticleServiceImpl implements IAnalysisArticleService {

    @Autowired
    private AnalysisArticleContext analysisArticleContext;

    @Autowired
    private IAnalysisUrlService analysisUrlService;

    @Autowired
    private LinkTaskMapper linkTaskMapper;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IRunTaskService runTaskService;

    @Autowired
    private IUniqueService uniqueService;

    @Autowired
    private CrawlCsdnCategoryService crawlCsdnCategoryService;


    /**
     * 直接爬取文章
     *
     * @param url
     * @throws Exception
     */
    @Override
    public void crawlerArticle(String url){
        RunTask runTask = null;
        try {
            // 解析url
            ArticleUrlInfo articleUrlInfo = analysisUrlService.parseUrl(url);
            runTask = runTaskService.saveRunTask(articleUrlInfo, (short) 1);
            Article article = analysisArticleContext.executeStrategy(articleUrlInfo, articleUrlInfo.getSourceType());
            // 先成功爬取，再校验是否存在
            boolean b = uniqueService.existArticleUrlInfo(articleUrlInfo);
            if (b){
                log.error("已经执行过相同地址的任务 url:{}");
                return;
            }
            // 不存在则保存文章
            articleService.saveArticle(article);
            uniqueService.saveArticleUrlInfo(articleUrlInfo);
            updateDataRunTask(null,runTask);
        } catch (Exception e) {
            log.error("爬取文章服务执行异常 url:{} error:{}",url,e.getMessage());
            updateDataRunTask(e,runTask);
            throw new CommonException(e);

        }
    }

    /**
     * 直接爬取文章
     *
     * @param url
     * @throws Exception
     */
    @Override
    public void crawlerArticle(String url,Integer topicId){
        RunTask runTask = null;
        try {
            // 解析url
            ArticleUrlInfo articleUrlInfo = analysisUrlService.parseUrl(url);
            runTask = runTaskService.saveRunTask(articleUrlInfo, (short) 1);
            Article article = analysisArticleContext.executeStrategy(articleUrlInfo, articleUrlInfo.getSourceType());
            article.setTopicId(topicId);
            // 先成功爬取，再校验是否存在
            boolean b = uniqueService.existArticleUrlInfo(articleUrlInfo);
            if (b){
                log.error("已经执行过相同地址的任务 url:{}");
                return;
            }
            // 不存在则保存文章
            articleService.saveArticle(article);
            uniqueService.saveArticleUrlInfo(articleUrlInfo);
            updateDataRunTask(null,runTask);
        } catch (Exception e) {
            log.error("爬取文章服务执行异常 url:{} error:{}",url,e.getMessage());
            updateDataRunTask(e,runTask);
            throw new CommonException(e);

        }
    }

    /**
     * 爬取文章
     *
     * @param linkTask
     * @throws Exception
     */
    @Override
    public void crawlerArticle(LinkTask linkTask) {
        if (StringUtils.isBlank(linkTask.getLinkUrl())){
            return;
        }
        try {
            // 解析url
            ArticleUrlInfo articleUrlInfo = analysisUrlService.parseUrl(linkTask.getLinkUrl());

            Article article = analysisArticleContext.executeStrategy(articleUrlInfo, articleUrlInfo.getSourceType());

            // 先成功爬取，再校验是否存在
            boolean b = uniqueService.existArticleUrlInfo(articleUrlInfo);
            if (b){
                log.error("已经执行过相同地址的任务 url:{}");
                return;
            }
            // 不存在则保存文章
            articleService.saveArticle(article);

            uniqueService.saveArticleUrlInfo(articleUrlInfo);
            updateDataLinkTask(null,linkTask);

        } catch (Exception e) {
            log.error("爬取文章服务执行异常 url:{} error:{}",linkTask.getLinkUrl(),e.getMessage());
            updateDataLinkTask(e,linkTask);
            throw new CommonException(e);

        }
    }

    /**
     * 扫描任务中的url，进行爬取操作
     */
    @Override
    public void scanCrawlerTask() {
        List<RunTask> runTasks = runTaskService.listCrawlerRunTask();
        if (CollectionUtils.isEmpty(runTasks)){
            log.info("未扫描到可以执行的任务");
        }
        runTasks.stream().forEach(s -> {
            try {
                // 异步修改为运行中
                runTaskService.updateRunTasking(s.getId());
                // 执行爬取任务
                crawlerArticle(s.getOriginalUrl());

                updateDataRunTask(null,s);
            } catch (Exception e) {
                updateDataRunTask(e,s);
            }
        });
    }

    /**
     * 获取文章分类的所有文章
     *
     * @param categoryUrl
     * @return
     */
    @Override
    public List<ArticleCategoryVO> crawlerArticleCategory(String categoryUrl) throws CommonException {
        try {
            boolean b = ArticleUtils.verifyCsdnCategoryUrl(categoryUrl);
            if (!b) {
                throw new CommonException("错误的文章分类地址 url:{}",categoryUrl);
            }

            List<ArticleCategoryVO> vos = crawlCsdnCategoryService.analysisArticleUrl(categoryUrl);
            if (CollectionUtils.isNotEmpty(vos)){
                vos.forEach(v -> {
                    try {
                        Thread.sleep(RandomUtil.randomInt(500,5000));
                    } catch (InterruptedException e) {
                    }
                    MyThreadPool.threadPool.execute(() -> {
                        crawlerArticle(v.getUrl(),v.getCategoryId());
                    });
                });
            }

            return vos;
        } catch (Exception e) {
            log.error("获取CSDN文章分类的所有文章异常 url：{} error:{}",categoryUrl,e.getMessage());
            throw new CommonException(e);

        }
    }


    public void updateDataRunTask(Throwable e,RunTask runTask){
        RunTask task = new RunTask()
                .setId(runTask.getId())
                .setRunStatus((short) 3)
                .setUpdateTime(new Date());
        if ( null != e){
            task.setRunStatus((short)2)
                    .setErrorMsg(StringComUtils.limitStrNone(e.getMessage(),300));
        }
        runTaskService.updateRunTask(task);
    }


    public void updateDataLinkTask(Throwable e,LinkTask linkTask){
        LinkTask task = new LinkTask()
                .setId(linkTask.getId())
                .setRunStatus((short) 3)
                .setUpdateTime(new Date());
        if ( null != e){
            task.setRunStatus((short)2)
                    .setErrorMsg(StringComUtils.limitStrNone(e.getMessage(),300));
        }
        linkTaskMapper.updateLinkTask(task);
    }
}
