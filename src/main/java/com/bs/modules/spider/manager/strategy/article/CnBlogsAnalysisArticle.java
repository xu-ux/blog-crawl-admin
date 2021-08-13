package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.http.OkHttpUtils;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import io.github.xucl.CodeBlockStyle;
import io.github.xucl.CopyDown;
import io.github.xucl.OptionsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName CnBlogsAnalysisArticle
 * @Description 博客园爬虫
 * @date 2021/8/4
 */
@Slf4j
@Component
public class CnBlogsAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {
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

        BeanUtils.copyProperties(urlInfo, article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("CnBlogs-START-爬取开始url:{}", originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            JXDocument jxDocument = JXDocument.create(document);
//            JXNode author = jxDocument.selNOne("//*[@id=\"topics\"]/div/div[3]/a[1]");
            JXNode author = jxDocument.selNOne("//*[@id=\"post_detail\"]/div/p/a[1]");
            if (null == author){
                //*[@id="topics"]/div[1]/div[3]/a[1]
                author = jxDocument.selNOne("//*[@id=\"topics\"]/div/div[3]/a[1]");
            }
            log.debug("author = " + author.asElement().text());

            Element time = document.getElementById("post-date");
            DateTime dateTime = DateUtil.parse(time.text(), DatePattern.NORM_DATETIME_MINUTE_PATTERN);
            log.debug("time = " + time.text());

            //Element titleEmt = document.getElementById("cb_post_title_url");
            Element titleEmt = document.selectFirst("#cb_post_title_url > span");
            log.debug("title = " + titleEmt.text());

            List<String> tag = getTag(document, originalUrl);
            log.debug("tagList = " + tag.toString());

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.getElementById("cnblogs_post_body");

            CopyDown converter = new io.github.xucl.CopyDown(OptionsBuilder.anOptions()
                    .withCodeBlockStyle(CodeBlockStyle.CNBLOGS)
                    .build());
            String markdown = converter.convert(contentViews.html());
            log.debug("markdown =\n " + markdown);
            // 图片转换
            String imgContentHtml = articleImgService.convertImgWithHtmlSecond(contentViews.html(), articleId);
            String imgMarkdown = converter.convert(imgContentHtml);

            article.setDigest(StringComUtils.limitStrNone(contentViews.text(), 500));
            article.setArticleId(articleId);
            article.setTag(tag.stream().collect(Collectors.joining(",")));
            article.setTitle(titleEmt.text());
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(contentViews.html());
            article.setHtmlContent(imgContentHtml);

            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(time.text());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.asElement().text());
            article.setCreateDate(new Date());

            log.info("CnBlogs-END-爬取完成url:{}，title:{}", originalUrl, titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("CnBlogs-ERROR-获取url的document异常 url:{}", originalUrl, e);
            throw new CommonException("CSDN-获取url的document异常 url:{} error:{}", originalUrl, e.getMessage());
        }
    }


    public List<String> getTag(Document document, String url) {
        String html = document.html();
        Pattern p1 = Pattern.compile("cb_blogId = (\\d+),");
        Pattern p2 = Pattern.compile("cb_entryId = (\\d+),");
        Pattern p3 = Pattern.compile("cb_blogApp = '([\\w\\-]+)',");
        Matcher m1 = p1.matcher(html);
        Matcher m2 = p2.matcher(html);
        Matcher m3 = p3.matcher(html);

        String blogId = "";
        String postId = "";
        String blogApp = "";
        if (m1.find()) {
            blogId = m1.group(1).trim();
        }
        if (m2.find()) {
            postId = m2.group(1).trim();
        }
        if (m3.find()) {
            blogApp = m3.group(1).trim();
        }
        log.info("url:{} blogId:{} postId:{}", url, blogId, postId);
        String s = OkHttpUtils.builder().url("https://www.cnblogs.com/" + blogApp + "/ajax/CategoriesTags.aspx")
                .addParam("blogId", blogId)
                .addParam("postId", postId)
                .addParam("_", String.valueOf(System.currentTimeMillis()))
                .get().sync();
        Document doc = Jsoup.parse(s);
        Elements elements1 = doc.select("#BlogPostCategory > a");
        Elements elements2 = doc.select("#EntryTag > a");

        List<String> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(elements1)) {
            List<String> list1 = elements1.stream().map(et -> et.text()).collect(Collectors.toList());
            list.addAll(list1);
        }
        if (CollectionUtils.isNotEmpty(elements2)) {
            List<String> list2 = elements2.stream().map(et -> et.text()).collect(Collectors.toList());
            list.addAll(list2);
        }
        return list;

    }
}
