package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.http.HttpClientUtils;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.dto.ImgPath;
import io.github.xucl.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class SFAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {



    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) throws CommonException {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("Segmentfault-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();
            JSONObject obj = parse(document.html());
            JSONObject json = obj.getJSONObject(urlInfo.getOriginalId());
            JSONObject articleJson = json.getJSONObject("article");


//            Element author = document.selectFirst("div > a > strong");
            JSONObject user = articleJson.getJSONObject("user");
            String username = user.getString("name");
            log.debug("author = " + username);

//            Element time = document.selectFirst("time");
//            DateTime dateTime = DateUtil.parse(time.attr("datetime"), DatePattern.UTC_MS_WITH_ZONE_OFFSET_PATTERN);
            Long created = articleJson.getLong("created");
            DateTime dateTime = DateUtil.date(created * 1000L);
            log.debug("time = " + dateTime.toString());

//            Element titleEmt = document.selectFirst("#root > div.article-content.container > div.row > div > div.card > div > h1 > a");
            String title = articleJson.getString("title");
            log.debug("title = " + title);

//            Elements tagNodes = document.select("#root > div.article-content.container > div.row > div > div.card > div > div:nth-child(5) > div > a");
//            List<String> tagList = tagNodes.stream().map(et -> et.text()).collect(Collectors.toList());
            String keywords = json.getString("keywords");
            log.debug("tagList = " + keywords);

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.getElementsByTag("article").get(0);

            CopyDown converter = new CopyDown();
            // 图片转换
            String html = contentViews.html();

            Elements elements = Jsoup.parse(html).select("img");
            Map<String, String> srcMap = elements.stream().map(e -> e.attr("data-src")).filter(StringUtils::isNotBlank)
                    .collect(Collectors.toMap(Function.identity(), (s -> "https://segmentfault.com".concat(s).concat("/view"))));
            for (Map.Entry<String, String> e : srcMap.entrySet()) {
                html = html.replace(e.getKey(), e.getValue());
            }
            ArrayList<String> srcList = new ArrayList<>(srcMap.values());

            String markdown = converter.convert(html);

            String imgContentHtml = articleImgService.convertImgWithUrl(srcList, html, articleId);
            String imgMarkdown = converter.convert(imgContentHtml);


            article.setDigest(json.getString("description"));
            article.setArticleId(articleId);
            article.setTag(keywords);
            article.setTitle(title);
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(html);

            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(dateTime.toString());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(username);
            article.setCreateDate(new Date());

            log.info("Segmentfault-END-爬取完成url:{}，title:{}",originalUrl,title);
            return article;

        } catch (Exception e) {
            log.error("Segmentfault-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("Segmentfault-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }


    /**
     * 简书获取json
     * @param htmlStr
     * @return
     */
    private JSONObject parse(String htmlStr){
        String regEJson = "window.g_initialProps\\s*=\\s*(.*)\\s*</script>";

        String regJson = "\"artDetail\":(.*)\\s*,\"alertMsg\"";
        Pattern p = Pattern.compile(regJson, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlStr);
        while (m.find()) {
            return JSON.parseObject(m.group(1));
        }
        return null;
    }
}
