package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringComUtils;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.http.HttpClientUtils;
import com.bs.modules.spider.manager.strategy.BaseStrategy;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import io.github.xucl.CodeBlockStyle;
import io.github.xucl.CopyDown;
import io.github.xucl.OptionsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @descriptions:
 * @author: xucl
 * @date: 2021/8/10
 * @version: 1.0
 */
@Slf4j
@Component
public class InfoQAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {

    private static final String doPostUrl = "https://xie.infoq.cn/public/v1/article/getDetail";
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

        // 创建ChromeDriver对象
        ChromeOptions options = new ChromeOptions();
        // 设置不显示窗口
        options.addArguments("--headless");

        options.addArguments("User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");

        //options.addArguments("--disable-gpu");
        ChromeDriver driver = new ChromeDriver(options);

        try {
            log.info("InfoQ-START-爬取开始url:{}",originalUrl);

            // 与浏览器同步非常重要，必须等待浏览器加载完毕
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            // 创建窗口最大化
            driver.manage().window().maximize();
            // 打开网页
            driver.get(originalUrl);

            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            //String html = (String) driver.executeScript("return document.documentElement.outerHTML");
            // 标题
            WebElement content = driver.findElement(By.cssSelector("div.ProseMirror"));
            String contentText = content.getText();
            String outerHTML = content.getAttribute("outerHTML");

            CopyDown copyDown = new io.github.xucl.CopyDown(OptionsBuilder.anOptions()
                    .withCodeBlockStyle(CodeBlockStyle.INFOQ)
                    .build());
            String originalMdContent = copyDown.convert(outerHTML);
            // log.debug("originalMdContent = " +originalMdContent);


            JSONObject json = HttpClientUtils.doPostJson(doPostUrl, getParams(urlInfo).toString(), getHeaders(urlInfo));
            JSONObject data = json.getJSONObject("data");

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();
            String imgHtml = articleImgService.convertImgWithHtmlSecond(outerHTML, articleId);
            String imgMarkdown = copyDown.convert(imgHtml);

            long publishTime = data.getLongValue("publish_time");
            DateTime dateTime = DateUtil.date(publishTime);

            JSONArray jsonArray = data.getJSONArray("author");
            JSONObject author = (JSONObject) jsonArray.get(0);

            JSONArray label = data.getJSONArray("label");
            String tagName = label.stream().map(s -> {
                JSONObject a = (JSONObject) s;
                return a.getString("name");
            }).collect(Collectors.joining(","));

            log.debug("title = " + data.getString("article_title"));
            log.debug("author = " + author.getString("nickname"));
            log.debug("time = " +dateTime.toString());

            article.setDigest(data.getString("article_summary"));
            article.setArticleId(articleId);
            article.setTag(tagName);
            article.setTextContent(contentText);
            article.setTitle(data.getString("article_title"));
            article.setOriginalHtmlContent(outerHTML);
            article.setOriginalMdContent(originalMdContent);
            article.setHtmlContent(imgHtml);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(dateTime.toString());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.getString("nickname"));
            article.setCreateDate(new Date());


            log.info("InfoQ-END-爬取结束url:{}，title:{}",originalUrl,data.getString("article_title"));
            return article;

        } catch (Exception e) {
            log.error("InfoQ-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("InfoQ-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        } finally {
            driver.quit();
        }
    }


    public JSONObject getParams(ArticleUrlInfo urlInfo){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid",urlInfo.getOriginalId());
        return jsonObject;
    }

    public Map<String, String> getHeaders(ArticleUrlInfo urlInfo){
        Map<String, String> map = new HashMap();
        map.put("Referer",urlInfo.getOriginalUrl());
        map.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
        return map;
    }
}
