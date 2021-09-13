package com.bs.modules.spider.manager.strategy.article;

import com.bs.common.config.proprety.LocalProperty;
import com.bs.common.exception.base.CommonException;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.enums.SourceType;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName AnalysisArticleContext
 * @Description
 * @date 2021/7/30
 */
@Slf4j
@Service
public class AnalysisArticleContext {

    @Autowired
    private CsdnAnalysisArticle csdnAnalysisArticle;

    @Autowired
    private JianShuAnalysisArticle jianShuAnalysisArticle;

    @Autowired
    private SeleniumAnalysisArticle seleniumAnalysisArticle;

    @Autowired
    private CnBlogsAnalysisArticle cnBlogsAnalysisArticle;

    @Autowired
    private WeiXinAnalysisArticle weiXinAnalysisArticle;

    @Autowired
    private CTOAnalysisArticle ctoAnalysisArticle;

    @Autowired
    private SFAnalysisArticle sfAnalysisArticle;

    @Autowired
    private JueJinAnalysisArticle jueJinAnalysisArticle;

    @Autowired
    private OsChinaAnalysisArticle osChinaAnalysisArticle;

    @Autowired
    private InfoQAnalysisArticle infoQAnalysisArticle;

    @Autowired
    private SmzdmAnalysisArticle smzdmAnalysisArticle;

    /**
     * 启动时，配置策略对象
     */
    @PostConstruct
    public synchronized void addStrategy() {
        list.add(csdnAnalysisArticle);
        list.add(jianShuAnalysisArticle);
        list.add(seleniumAnalysisArticle);
        list.add(cnBlogsAnalysisArticle);
        list.add(weiXinAnalysisArticle);
        list.add(csdnAnalysisArticle);
        list.add(ctoAnalysisArticle);
        list.add(sfAnalysisArticle);
        list.add(jueJinAnalysisArticle);
        list.add(osChinaAnalysisArticle);
        list.add(infoQAnalysisArticle);
        list.add(smzdmAnalysisArticle);
    }

    @Autowired
    private LocalProperty localProperty;

    private static volatile List<AnalysisArticleStrategy> list = new ArrayList<>();


    /**
     * 文章爬取上下文匹配并执行策略
     * @param urlInfo
     * @param sourceType
     * @return
     */
    public Article executeStrategy(ArticleUrlInfo urlInfo, SourceType sourceType) throws CommonException{
        try {
            AnalysisArticleStrategy service = findService(sourceType);
            return service.doOperation(urlInfo);
        } catch (Exception e) {
            log.error("文章爬取上下文匹配并执行策略异常 error:{}",e.getMessage());
            throw new CommonException(e);
        }
    }


    /**
     * 通过sourceType获取相应的策略
     * @param sourceType
     * @return
     */
    public AnalysisArticleStrategy findService(SourceType sourceType){
        final String name = sourceType.getClazz().getName();
        return list.stream().filter(s -> s.getClass().getName().equals(name)).findFirst().orElse(null);
    }


    /**
     * 启动时，配置驱动路径
     */
    @PostConstruct
    public synchronized void init(){
        String osName = System.getProperty("os.name").toLowerCase();
        String path = "";
        if (osName.indexOf("win") != -1){
            // 设置驱动
            path = ClassUtils.getDefaultClassLoader().getResource(localProperty.getDriverWin()).getPath();
        } else {
            path = ClassUtils.getDefaultClassLoader().getResource(localProperty.getDriverLinux()).getPath();
        }
        System.setProperty("webdriver.chrome.driver", path);
    }
}
