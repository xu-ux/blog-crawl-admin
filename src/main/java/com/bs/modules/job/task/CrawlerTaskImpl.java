package com.bs.modules.job.task;

import com.bs.modules.job.handler.base.BaseTaskService;
import com.bs.modules.spider.service.IAnalysisArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName CrawlerTaskImpl
 * @Description
 * @date 2021/8/3 9:37
 */
@Slf4j
@Component("crawlerTask")
public class CrawlerTaskImpl  implements BaseTaskService {


    @Autowired
    private IAnalysisArticleService analysisArticleService;


    /**
     * 任 务 实 现
     *
     * @param params
     */
    @Override
    public void run(String params) throws Exception {
        analysisArticleService.scanCrawlerTask();
    }

}
