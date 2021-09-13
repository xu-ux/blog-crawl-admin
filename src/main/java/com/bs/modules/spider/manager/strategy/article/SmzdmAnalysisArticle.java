package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @descriptions: 值得买社区
 * @author: xucl
 * @date: 2021/9/13 15:40
 * @version: 1.0
 */
@Slf4j
@Component
public class SmzdmAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy{

    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) throws CommonException {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("Zdm-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            Element author = document.selectFirst("#feed-side > div:nth-child(1) > div.user_tx > div > div > h2 > a");
            log.debug("author = " + author.text());

            Element time = document.selectFirst("#articleId > div.recommend-tab.z-clearfix.item-preferential > span > span:nth-child(1)");
            DateTime dateTime = DateUtil.parse(time.text(), DatePattern.NORM_DATETIME_PATTERN);
            log.debug("time = " + dateTime.toString());

            Element titleEmt = document.selectFirst("#articleId > h1");
            log.debug("title = " + titleEmt.text());


            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.selectFirst("#articleId");

            CopyDown converter = new CopyDown();
            String markdown = converter.convert(contentViews.html());
            // 图片转换
            String imgContentHtml = articleImgService.convertImgWithHtml(contentViews.html(), articleId);
            String imgMarkdown = converter.convert(imgContentHtml);

            article.setDigest(StringComUtils.limitStrNone(contentViews.text(),500));
            article.setArticleId(articleId);
            article.setTag("");
            article.setTitle(titleEmt.text());
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(contentViews.html());
            article.setHtmlContent(imgContentHtml);

            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(time.text());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.text());
            article.setCreateDate(new Date());

            log.info("Zdm-END-爬取完成url:{}，title:{}",originalUrl,titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("Zdm-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("Zdm-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }
}
