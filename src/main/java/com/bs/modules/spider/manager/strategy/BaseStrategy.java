package com.bs.modules.spider.manager.strategy;

import com.bs.common.config.proprety.LocalProperty;
import com.bs.modules.spider.service.IArticleImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName AnalysisBaseStrategy
 * @Description 通用的公共策略基础依赖
 * @date 2021/8/2
 */
public abstract class BaseStrategy {

    @Autowired
    protected IArticleImgService articleImgService;

    @Autowired
    protected LocalProperty localProperty;

}
