package com.bs.modules.spider.enums;

import com.bs.modules.spider.manager.strategy.article.CnBlogsAnalysisArticle;
import com.bs.modules.spider.manager.strategy.article.CsdnAnalysisArticle;
import com.bs.modules.spider.manager.strategy.article.JianShuAnalysisArticle;
import com.bs.modules.spider.manager.strategy.article.SeleniumAnalysisArticle;

import java.util.Arrays;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName OriginalType
 * @Description
 * @date 2021/8/5
 */
public enum OriginalType {

    /**
     * 微信公众号手机分享
     */
    WeiXin(1,"微信公众号"),
    /**
     * 支持自定义域名
     */
    CSDN(2,"csdn论坛"),
    /**
     * id可以自定义，官方数字id
     */
    CNBlogs(3,"博客园"),
    SF(4,"思否"),
    JueJin(5,"掘金"),
    JianShu(6,"简书"),
    Modi(7,"墨滴"),
    OsChina(8,"开源中国博客"),
    CTO(9,"51CTO博客"),
    PoJie(10,"吾爱破解"),
    KanXue(11,"看雪安全论坛"),
    Stackoverflow(12,"栈溢出论坛"),
    InfoQ(13,"infoQ中文站博客"),
    TencentCloud(14,"腾讯云社区"),
    ZhiDeMai(15,"值得买社区"),
    ;
    /**
     * typeId
     */
    private int id;
    /**
     * 名称
     */
    private String name;


    public static OriginalType ofId(int id){
        return Arrays.asList(OriginalType.values()).stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    OriginalType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public OriginalType setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OriginalType setName(String name) {
        this.name = name;
        return this;
    }
}
