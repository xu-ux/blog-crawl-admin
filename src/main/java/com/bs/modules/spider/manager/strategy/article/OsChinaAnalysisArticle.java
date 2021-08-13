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
 * @descriptions:
 * @author: xucl
 * @date: 2021/8/10
 * @version: 1.0
 */
@Slf4j
@Component
public class OsChinaAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {
    /**
     * 执行爬取文章逻辑
     *
     * @param urlInfo
     * @return
     */
    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) throws CommonException {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("OsChina-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            Element author = document.selectFirst("div.article-box__header > div.article-box__meta > div > div:nth-child(2) > a");
            log.debug("author = " + author.text());

            Element time = document.selectFirst("div.article-box__header > div.article-box__meta > div > div:nth-child(4)");
            String timeText = time.text();
            DateTime dateTime = DateTime.now();
            if (timeText.startsWith("2")){
                dateTime = DateUtil.parse(timeText, "yyyy/MM/dd HH:mm");
            } else {
                dateTime = DateUtil.parse(timeText, "MM/dd HH:mm");
                dateTime.setYear(DateTime.now().getYear());
            }
            log.debug("time = " + dateTime.toString());

            Element titleEmt = document.selectFirst(".article-box__title > a");
            log.debug("title = " + titleEmt.text());


            Elements tagEmts = document.select("div.article-box__content > div.tags-box > div.tags-box__inner > a");
            List<String> tagList = tagEmts.stream().map(et -> et.text()).collect(Collectors.toList());
            log.debug("tagList = " + tagList.toString());

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.selectFirst(".article-detail > .content");

            CopyDown converter = new CopyDown();
            String markdown = converter.convert(contentViews.html());
            // 图片转换
            String imgContentHtml = articleImgService.convertImgWithHtml(contentViews.html(), articleId);
            String imgMarkdown = converter.convert(imgContentHtml);

            article.setDigest(StringComUtils.limitStrNone(contentViews.text(),500));
            article.setArticleId(articleId);
            article.setTag(tagList.stream().collect(Collectors.joining(",")));
            article.setTitle(titleEmt.text());
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(contentViews.html());
            article.setHtmlContent(imgContentHtml);

            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(timeText);
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.text());
            article.setCreateDate(new Date());

            log.info("OsChina-END-爬取完成url:{}，title:{}",originalUrl,titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("OsChina-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("OsChina-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }
}
