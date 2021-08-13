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
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @descriptions: 掘金博客分析
 * @author: xucl
 * @date: 2021/8/10
 * @version: 1.0
 */
@Slf4j
@Component
public class JueJinAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {
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
            log.info("JueJin-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            Element author = document.selectFirst("#juejin > div.view-container > main > div > div > article > div:nth-child(5) > meta:nth-child(1)");
            log.debug("author = " + author.attr("content"));

            Element time = document.selectFirst("time.time");
            DateTime dateTime = DateUtil.parse(time.attr("datetime"), DatePattern.UTC_MS_FORMAT);
            log.debug("time = " + dateTime.toString());

            Element titleEmt = document.selectFirst(".article-title");
            log.debug("title = " + titleEmt.text());

            Elements tagEmts = document.select(".tag-list-container > a > .tag-title");
            List<String> tagList = tagEmts.stream().map(et -> et.text()).collect(Collectors.toList());
            log.debug("tagList = " + tagList.toString());

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.selectFirst(".markdown-body");

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
            article.setOriginalDateStr(time.text());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.attr("content"));
            article.setCreateDate(new Date());

            log.info("JueJin-END-爬取完成url:{}，title:{}",originalUrl,titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("JueJin-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("JueJin-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }

}
