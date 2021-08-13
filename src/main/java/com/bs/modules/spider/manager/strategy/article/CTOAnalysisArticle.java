package com.bs.modules.spider.manager.strategy.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class CTOAnalysisArticle extends BaseStrategy implements AnalysisArticleStrategy {



    @Override
    public Article doOperation(ArticleUrlInfo urlInfo) throws CommonException {
        checkUrInfo(urlInfo);
        Article article = new Article();

        BeanUtils.copyProperties(urlInfo,article);
        // jsoup获取element
        String originalUrl = urlInfo.getOriginalUrl();
        try {
            log.info("51CTO-START-爬取开始url:{}",originalUrl);

            Document document = Jsoup.connect(originalUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                    .get();

            JXDocument jxDocument = JXDocument.create(document);
            Element author = document.selectFirst("div.messbox > p.mess-line1 > a");
            //JXNode author = jxDocument.selNOne("/html/body/div[2]/div[1]/div[2]/section/article[1]/div[2]/p[1]/a");
            log.debug("author = " + author.text());

            // JXNode time = jxDocument.selNOne("/html/body/div[2]/div[1]/div[2]/section/article[1]/div[2]/p[1]/time");
            //<time class="fl" pubdate="2021-07-30 09:56:44">2021-07-30 09:56:44</time>
            Element time = document.selectFirst("div.messbox > p.mess-line1 > time");
            DateTime dateTime = DateUtil.parse(time.text(), DatePattern.NORM_DATETIME_PATTERN);
            log.debug("time = " + time.text());


            Element titleEmt = document.selectFirst("div.title > h1");
            //JXNode titleEmt = jxDocument.selNOne("/html/body/div[2]/div[1]/div[2]/section/article[1]/div[1]/h1");
            log.debug("title = " + titleEmt.text());

            //JXNode tagNode = jxDocument.selNOne("/html/body/div[2]/div[1]/div[2]/section/article[1]/div[2]/p[2]/strong[1]");
            Elements tagNodes = document.select("div.messbox > p.mess-tag> strong:nth-child(1) > a ");
            List<String> tagList = tagNodes.stream().map(et -> et.text()).collect(Collectors.toList());
            log.debug("tagList = " + tagList.toString());

            // 文章主键
            long articleId = IdUtil.createSnowflake(1, 1).nextId();

            Element contentViews = document.getElementById("markdownContent");

            //CopyDown converter = new CopyDown();
            String markdown = getMdContent(document.html());
            // 图片转换
            String imgMarkdown = articleImgService.convertImgWithMD(markdown,articleId);

            article.setDigest(StringComUtils.limitStrNone(contentViews.text(),500));
            article.setArticleId(articleId);
            article.setTag(tagList.stream().collect(Collectors.joining(",")));
            article.setTitle(titleEmt.text());
            article.setTextContent(contentViews.text());
            article.setOriginalHtmlContent(contentViews.html());

            article.setOriginalMdContent(markdown);
            article.setMdContent(imgMarkdown);
            article.setOriginalDateStr(time.text());
            article.setOriginalDate(dateTime);
            article.setOriginalAuthor(author.text());
            article.setCreateDate(new Date());

            log.info("51CTO-END-爬取完成url:{}，title:{}",originalUrl,titleEmt.text());
            return article;

        } catch (Exception e) {
            log.error("51CTO-ERROR-获取url的document异常 url:{}",originalUrl,e);
            throw new CommonException("51CTO-获取url的document异常 url:{} error:{}",originalUrl,e.getMessage());
        }
    }

    /**
     * 获取实际md内容
     * @param htmlStr
     * @return
     */
    private String getMdContent(String htmlStr){
        String regEJson = "insertCodeElement\\(\"(.*)\", '#result'\\);";
        Pattern p = Pattern.compile(regEJson, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlStr);
        while (m.find()) {
            String toString = UnicodeUtil.toString(m.group(1));
            toString = toString.replace("\\n\\n\\n\\n\\n","\n\n\n\n");
            toString = toString.replace("\\n\\n\\n\\n","\n\n\n");
            toString = toString.replace("\\n\\n","\n");
            toString = toString.replace("\\n","\n");
            toString = toString.replace("\\/","/");
            return toString;
        }
        return "";
    }


}
