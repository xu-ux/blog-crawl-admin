package com.bs.modules.spider.manager.strategy.article;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName WeiXinAnalysisArticle
 * @Description
 * @date 2021/8/5
 */
@Slf4j
@Component
public class WeiXinAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy{

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

        String originalUrl = urlInfo.getOriginalUrl();
        // 创建ChromeDriver对象
        ChromeOptions options = new ChromeOptions();
        // 设置不显示窗口
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        ChromeDriver driver = new ChromeDriver(options);
        try {
            log.info("WeiXin-START-爬取开始url:{}",originalUrl);

            // 与浏览器同步非常重要，必须等待浏览器加载完毕
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            // 创建窗口最大化
            driver.manage().window().maximize();
            // 打开网页
            driver.get(originalUrl);
            // 执行这段代码，会获取到当前窗口总高度
            String heightJs = "return action = document.body.scrollHeight;";
            // 初始化现在滚动条所在高度为0
            Integer height = 0;
            // 当前窗口总高度
            Long newHeight = (Long) driver.executeScript(heightJs);
            while(height < newHeight){
                height += 100;
                driver.executeScript(String.format("window.scrollTo(0, %s)",height));
                Thread.sleep(200);
            }
            Thread.sleep(1000);
            // 作者
            WebElement author = driver.findElementById("js_name");
            log.debug("author = " + author.getText());

            // 标题
            WebElement titleElement = driver.findElementById("activity-name");
            String title = titleElement.getText();
            log.debug("title = " +title);

            long articleId = IdUtil.createSnowflake(1, 1).nextId();
            // 内容
            WebElement articleContent = driver.findElement(By.id("js_content"));
//            String outerHTML = articleContent.getAttribute("outerHTML");
            Document documentOut = Jsoup.parse(articleContent.getAttribute("outerHTML"));
            String outerHTMLClean = cleanWeiXinStyles(documentOut.body());

            // 时间
            //WebElement publishTime = driver.findElementById("publish_time");
            String html = (String) driver.executeScript("return document.documentElement.outerHTML");
            String dateStr = matchDateStr(html);
            DateTime publishTime = convertWeiXinTime(dateStr);
            log.debug("time = " +publishTime.toString());

            // 图片转换
            String imgContentHtml = articleImgService.convertImgWithHtmlSecond(outerHTMLClean, articleId);
            //Document document = Jsoup.parse(imgContentHtml);
            //String imgContentHtmlClean = cleanWeiXinStyles(document.body());

            CopyDown converter = new CopyDown();
            String markdown = converter.convert(outerHTMLClean);
            String imgMarkdown = converter.convert(imgContentHtml);

            article.setDigest(StringComUtils.limitStrNone(articleContent.getText(),500));
            article.setArticleId(articleId);
            article.setTag("");
            article.setTitle(title);
            article.setTextContent(articleContent.getText());
            article.setOriginalHtmlContent(outerHTMLClean);
            article.setHtmlContent(imgContentHtml);
            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(publishTime.toString());
            article.setOriginalDate(publishTime);
            article.setOriginalAuthor(author.getText());
            article.setCreateDate(new Date());

            driver.quit();
            log.info("WeiXin-END-爬取结束url:{}，title:{}",originalUrl,title);
            return article;


        } catch (Exception e) {
            log.error("WeiXin-ERROR-动态爬取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("WeiXin-动态爬取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        } finally {
            driver.quit();
        }
    }

    /**
     *  2020-11-16
     *  7月1日
     *  今天
     *  n\s*=\s*"(\d{10})",
     * @param dateStr
     * @return
     */
    public DateTime convertWeiXinTime(String dateStr){
        DateTime date = DateUtil.date(Long.valueOf(dateStr) * 1000L);
        return date;
    }


    public String matchDateStr(String html){
        Pattern pattern = Pattern.compile("n\\s*=\\s*\"(\\d{10})\",");
        Matcher matcher = pattern.matcher(html);
        String dateStr = "";
        if (matcher.find()){
            dateStr = matcher.group(1).trim();
        }
        return dateStr;
    }

}
