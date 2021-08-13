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
 * @Author xucl
 * @Version 1.0
 * @ClassName JsoupAnalysisArticle
 * @Description jsup方式爬取内容
 * @date 2021/7/30
 */
@Slf4j
@Component
public class CsdnAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {



    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) throws CommonException {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("CSDN-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            JXDocument jxDocument = JXDocument.create(document);
            JXNode author = jxDocument.selNOne("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/a[1]");
            log.debug("author = " + author.asElement().text());

            JXNode time = jxDocument.selNOne("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/span[1]");
            //log.debug("time = " + jxNode.asString()); // <span class="time">2018-05-13 23:52:51</span>
            DateTime dateTime = DateUtil.parse(time.asElement().text(), DatePattern.NORM_DATETIME_PATTERN);
            log.debug("time = " + time.asElement().text());

            Element titleEmt = document.getElementById("articleContentId");
            log.debug("title = " + titleEmt.text());

            Elements tagEmts = document.getElementsByClass("tag-link");
            List<String> tagList = tagEmts.stream().map(et -> et.text()).collect(Collectors.toList());
            log.debug("tagList = " + tagList.toString());

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.getElementById("content_views");

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
            article.setOriginalDateStr(time.asElement().text());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.asElement().text());
            article.setCreateDate(new Date());

            log.info("CSDN-END-爬取完成url:{}，title:{}",originalUrl,titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("CSDN-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("CSDN-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }


}
