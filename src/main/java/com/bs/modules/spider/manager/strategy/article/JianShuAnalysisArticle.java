package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import io.github.xucl.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName JianShuAnalysisArticle
 * @Description 简书爬取策略
 * @date 2021/8/2
 */
@Slf4j
@Component
public class JianShuAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {


    /**
     * 执行爬取文章逻辑
     *
     * @param urlInfo
     * @return
     */
    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {

            log.info("JIANSHU-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();
            JXDocument jxDocument = JXDocument.create(document);

            JXNode author = jxDocument.selNOne("//*[@id=\"__next\"]/header/div[1]/div[2]/div/div[2]/a/span");
            log.debug("author = " + author.asElement().text());

            JSONObject jsonObject = parse(document.html());
            Long aLong = jsonObject.getJSONObject("props").getJSONObject("initialState").getJSONObject("note").getJSONObject("data").getLong("last_updated_at");
            DateTime lastUpdateTime = DateUtil.date(aLong * 1000L);
            log.debug("time = " +lastUpdateTime.toString());

            JXNode title = jxDocument.selNOne("//*[@id=\"__next\"]/div[1]/div/div/section[1]/h1");
            log.debug("title = " + title.asElement().text());
            // jsonObject.getJSONObject("props").getJSONObject("initialState").getJSONObject("note").getJSONObject("data").getString("public_title");

            // 动态刷新
            //JXNode tagListNode = jxDocument.selNOne("//*[@id=\"__next\"]/div[1]/div/div[1]/section[1]/div[3]/div[2]/a/span");
            //log.debug("tagList = " + tagListNode.asElement().text());

            long articleId = IdUtil.createSnowflake(1, 1).nextId();
            Element contentViews = document.getElementsByTag("article").get(0);

            // 去除span的内联样式

            CopyDown converter = new CopyDown();
            String markdown = converter.convert(contentViews.html());
            // 图片转换
            String imgContentHtml = articleImgService.convertImgWithHtmlSecond(contentViews.html(), articleId);
            String imgMarkdown = converter.convert(imgContentHtml);

            article.setDigest(StringComUtils.limitStrNone(contentViews.text(),500));
            article.setArticleId(articleId);
            article.setTag("");
            article.setTitle(title.asElement().text());
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(contentViews.html());
            article.setHtmlContent(imgContentHtml);
            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(lastUpdateTime.toString());
            article.setOriginalDate(lastUpdateTime);
            article.setOriginalAuthor(author.asElement().text());
            article.setCreateDate(new Date());

            log.info("JIANSHU-END-爬取结束url:{}，title:{}",originalUrl,title.asElement().text());
            return article;
        } catch (Exception e){
            log.error("JIANSHU-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("JIANSHU-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }


    /**
     * 简书获取json
     * @param htmlStr
     * @return
     */
    private JSONObject parse(String htmlStr){
        String regEJson = "<script id=\"__NEXT_DATA__\" type=\"application/json\">(.*)</script>";
        Pattern p = Pattern.compile(regEJson, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlStr);
        while (m.find()) {
            return JSON.parseObject(m.group(1));
        }
        return null;
    }
}
