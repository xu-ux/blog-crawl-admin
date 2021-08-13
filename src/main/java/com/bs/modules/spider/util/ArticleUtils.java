package com.bs.modules.spider.util;

import com.bs.common.exception.base.BusinessException;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleUtils
 * @Description
 * @date 2021/8/2
 */
public class ArticleUtils {


    /**
     * 校验url信息是否全部正确
     * @param urlInfo
     */
    public static void checkUrInfo(ArticleUrlInfo urlInfo) {
        if (StringUtils.isAnyBlank(urlInfo.getOriginalId(),
                urlInfo.getOriginalUrl(),
                urlInfo.getOriginalType().toString())){
            throw new BusinessException("校验url信息未通过");
        }
    }


    /**
     * 得到网页中图片的地址
     */
    public static List<String> getImgStr(String htmlStr) {
        List<String> pics = new ArrayList<String>();
        String img = "";
        Pattern pImage;
        Matcher mImage;
        String regExImg = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        pImage = Pattern.compile
                (regExImg, Pattern.CASE_INSENSITIVE);
        mImage = pImage.matcher(htmlStr);
        while (mImage.find()) {
            // 得到<img />数据
            img = mImage.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("(img\\s*)([\\w\\-]*src\\s*=\\s*\")(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                String group = m.group(3);
                if (StringUtils.isNotBlank(group)
                        && (group.contains("//") || group.contains("http"))){
                    pics.add(group);
                }
            }
        }
        return pics;
    }


    public static String matchImgUrl(String imgUrl){
        String regExImg = "^(http|https)://[.\\w\\-_/]*";
        Pattern p1 = Pattern.compile(regExImg);
        Matcher m1 = p1.matcher(imgUrl);
        if (!m1.find()){
            if (imgUrl.indexOf("//") == -1){
                return "https://".concat(imgUrl);
            } else {
                return "https:".concat(imgUrl);
            }
        }
        return imgUrl;
    }


    /**
     * 校验是否是csdn分类列表
     * @param url
     * @return
     */
    public static boolean verifyCsdnCategoryUrl(String url){
        String regExImg = "^(http|https)://([\\w.\\-_/]+/)(\\w+)(\\.html|\\.htm)";
        Pattern p1 = Pattern.compile(regExImg);
        Matcher m1 = p1.matcher(url);
        if (m1.find()){
            return true;
        }
        return false;
    }

    /**
     * 匹配网络图片后缀名
     *
     * @param imgUrl
     * @return
     */
    public static String matchImgSuffix(String imgUrl){
        if (StringUtils.isBlank(imgUrl)){
            return "";
        }
        if (imgUrl.toLowerCase().contains(".jpg")){
            return ".jpg";
        }
        if (imgUrl.toLowerCase().contains(".png")){
            return ".png";
        }
        if (imgUrl.toLowerCase().contains(".jpeg")){
            return ".jpeg";
        }
        if (imgUrl.toLowerCase().contains(".gif")){
            return ".gif";
        }
        if (imgUrl.toLowerCase().contains(".bmp")){
            return ".bmp";
        }
        if (imgUrl.toLowerCase().contains(".webp")){
            return ".webp";
        }
        return ".png";
    }


}
