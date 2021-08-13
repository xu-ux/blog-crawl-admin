package com.bs.modules.spider.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.bs.common.config.MyThreadPool;
import com.bs.common.exception.base.BusinessException;
import com.bs.common.tools.common.BeanHelper;
import com.bs.common.tools.common.ReadYmlUtils;
import com.bs.common.tools.oss.OssConst;
import com.bs.common.tools.oss.OssUtils;
import com.bs.modules.spider.domain.ArticleImg;
import com.bs.modules.spider.http.HttpClientUtils;
import com.bs.modules.spider.mapper.ArticleImgMapper;
import com.bs.modules.spider.pojo.dto.ArticleImgInfo;
import com.bs.modules.spider.pojo.dto.ImgPath;
import com.bs.modules.spider.service.IArticleImgService;
import com.bs.modules.spider.service.ILocalFileService;
import com.bs.modules.spider.util.ArticleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ArticleImgServiceImpl
 * @Description
 * @date 2021/8/2
 */
@Slf4j
@Service
public class ArticleImgServiceImpl implements IArticleImgService {

    @Autowired
    private ArticleImgMapper articleImgMapper;

    @Autowired
    private ILocalFileService localFileService;

    /**
     * 需要排除的地址
     */
    private static List<String> excludeUrl = new ArrayList<>();

    static {
        excludeUrl.add("//common.cnblogs.com/images/copycode.gif");
        excludeUrl.add("https://common.cnblogs.com/images/copycode.gif");
    }


    /**
     * 转换图片为oss图片
     *
     * @param contentHtml 文章区域的html
     * @param articleId
     * @return
     */
    @Override
    public String convertImgWithHtml(String contentHtml, Long articleId) {
        Elements elements = Jsoup.parse(contentHtml).select("img");
        if (CollectionUtils.isNotEmpty(elements)) {
            List<String> imgUrls = elements.stream().map(el -> el.attr("src")).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(imgUrls)) {
                return contentHtml;
            }
            return replaceImgAndSave(imgUrls,articleId,contentHtml);
        }
        return contentHtml;

    }

    /**
     * 转换图片为oss图片
     *
     * @param imgUrls 图片src地址
     * @param articleId
     * @return
     */
    @Override
    public String convertImgWithUrl(List<String> imgUrls,String contentHtml,Long articleId)   throws RuntimeException{
        if (CollectionUtils.isEmpty(imgUrls)) {
            return contentHtml;
        }
        return replaceImgAndSave(imgUrls,articleId,contentHtml);
    }

    /**
     * 转换Md的图片为oss图片或本地图片
     *
     * @param md markdown文本
     * @return
     */
    @Override
    public String convertImgWithMD(String md, Long articleId)  throws RuntimeException{
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
        Matcher m = pattern.matcher(md);
        List<String> imgUrls = new ArrayList<>();
        while (m.find()) {
            imgUrls.add(m.group(2));
        }
        if (CollectionUtils.isEmpty(imgUrls)) {
            return md;
        }
        return replaceImgAndSave(imgUrls,articleId,md);
    }

    /**
     * 转换图片为oss图片或本地图片
     *
     * @param contentHtml 文章区域的html
     * @param articleId
     * @return
     */
    @Override
    public String convertImgWithHtmlSecond(String contentHtml, Long articleId) throws RuntimeException {
        List<String> imgUrls = ArticleUtils.getImgStr(contentHtml);
        if (CollectionUtils.isEmpty(imgUrls)) {
            return contentHtml;
        }
        return replaceImgAndSave(imgUrls,articleId,contentHtml);
    }

    /**
     * 替换并保存图片
     *
     * @param imgUrls 图片地址
     * @param articleId 文章id
     * @param content 替换内容
     * @return
     */
    public String replaceImgAndSave(List<String> imgUrls,Long articleId,String content) throws BusinessException {
        try {
            imgUrls.removeAll(excludeUrl);
            if (CollectionUtils.isEmpty(imgUrls)){
                return content;
            }
            // 上传至oss或本地
            Map<String, ImgPath> pathMap = uploadOssImgAndLocal(imgUrls,articleId);
            if (MapUtils.isEmpty(pathMap)){
                return content;
            }
            // 实体装配
            List<ArticleImgInfo> articleImgInfos = convertArticleImgInfo(pathMap.values(), articleId);
            // 保存至数据库
            saveArticleImg(articleImgInfos, articleId);
            // 替换图片
            for (Map.Entry<String, ImgPath> e : pathMap.entrySet()) {
                if (ReadYmlUtils.getConfig().getAppSwitch().isUploadOss()){
                    content = content.replace(e.getKey(), e.getValue().getOssUrl());
                } else {
                    content = content.replace(e.getKey(), e.getValue().getLocalKey());
                }
            }
            return content;
        } catch (Exception e) {
            log.error("上传并转存图片异常",e);
            throw new BusinessException("上传并转存图片异常");
        }
    }

    /**
     * 保存图片信息
     *
     * @param infoList
     */
    @Override
    public void saveArticleImg(List<ArticleImgInfo> infoList, Long articleId) {
        if (CollectionUtils.isNotEmpty(infoList)) {
            List<ArticleImg> imgs = BeanHelper.copyList(infoList, ArticleImg.class);
            articleImgMapper.insertList(imgs);
            return;
        }
        log.info("不存在图片信息，无需存储 articleId：{}", articleId);
    }

    /**
     * 批量上传图片至本地和oss
     *
     * @param imgUrls 原始链接
     * @return
     */
    @Override
    public Map<String, ImgPath> uploadOssImgAndLocal(List<String> imgUrls,Long articleId) throws InterruptedException {

        Map<String, ImgPath> imgPath = new HashMap<>();
        List<String> imgSrc = imgUrls.stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
        final CountDownLatch latch = new CountDownLatch(imgSrc.size());
        imgSrc.forEach(url -> {
            Future<Boolean> submit = MyThreadPool.threadPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        Thread.sleep(RandomUtil.randomInt(10,100));
                        byte[] bytes = HttpClientUtils.downloadImage(ArticleUtils.matchImgUrl(url));
                        double fileSize = bytes.length / 1024d;

                        // 创建目录 和 文件key
                        String fileSuffix = ArticleUtils.matchImgSuffix(url);
                        String fileNewName = articleId.toString().concat("-").concat(RandomUtil.randomString(8)).concat(fileSuffix);
                        String dir = OssConst.getCrawleUploadImgDir();
                        String fileKey = dir + fileNewName;

                        InputStream in = new ByteArrayInputStream(bytes);

                        // 上传oss
                        String ossUrl = "";
                        if (ReadYmlUtils.getConfig().getAppSwitch().isUploadOss()){
                           OssUtils.uploadInsToOss(OssConst.getPublicBucketName(), dir, fileNewName, in, null);
                        }

                        // 上传本地
                        String localKey = localFileService.uploadImage(bytes, fileKey);

                        imgPath.put(url, new ImgPath().setOldUrl(url).setOssKey(fileKey).setOssUrl(ossUrl).setFileSize(fileSize)
                                .setLocalKey(localKey));


                        return true;
                    } catch (Exception e) {
                        log.error("批量保存图片异常 url:{}",url,e);
                        return false;
                    } finally {
                        latch.countDown();
                    }
                }
            });
        });
        latch.await();
        return imgPath;

    }


    /**
     * 转换实体信息
     *
     * @param imgPathList
     * @return
     */
    private List<ArticleImgInfo> convertArticleImgInfo(
            Collection<ImgPath> imgPathList, Long articleId) {
        return imgPathList.stream().filter(Objects::nonNull).map(img -> new ArticleImgInfo()
                .setArticleId(articleId)
                .setImgId(IdUtil.getSnowflake(3, 1).nextId())
                .setImgKey(img.getOssKey())
                .setImgSize(img.getFileSize())
                .setOriginalUrl(img.getOldUrl())
                .setImgUrl(img.getOssUrl())
                .setLocalImgKey(img.getLocalKey()))
                .collect(Collectors.toList());
    }
}
