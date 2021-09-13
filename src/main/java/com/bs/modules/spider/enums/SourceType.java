package com.bs.modules.spider.enums;

import com.bs.modules.spider.manager.strategy.article.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName SourceType
 * @Description
 * @date 2021/7/30
 */
public enum SourceType {
    /**
     * 微信公众号手机分享
     */
    WeiXin(1,"weixin","https://mp.weixin.qq.com/","weixin.qq.com","^(http|https)://([\\w.]+\\/s\\/)(\\w+)",3,"微信公众号", WeiXinAnalysisArticle.class),
    /**
     * 支持自定义域名
     */
    CSDN(2,"csdn","https://blog.csdn.net/","blog.csdn.net","^(http|https)://([\\w.\\-_\\/]+\\/)(\\d+)",3,"csdn论坛", CsdnAnalysisArticle.class),
    /**
     * id可以自定义，官方数字id
     */
    CNBlogs(3,"cnblogs","https://www.cnblogs.com/","cnblogs.com","^(http|https)://([\\w.\\-\\/]+p?\\/)([\\w\\-]+)(\\.html|\\.htm)$",3,"博客园", CnBlogsAnalysisArticle.class),
    SF(4,"segmentfault","https://segmentfault.com/","segmentfault.com","^(http|https)://([\\w.\\/]+\\/a\\/)(\\d+)",3,"思否",SFAnalysisArticle.class),
    JueJin(5,"juejin","https://juejin.cn/","juejin.cn","^(http|https)://([\\w.\\/]+post\\/)(\\d+)",3,"掘金",JueJinAnalysisArticle.class),
    JianShu(6,"jianshu","https://www.jianshu.com/","jianshu.com","^(http|https)://([\\w.\\/]+p\\/)(\\w+)",3,"简书", JianShuAnalysisArticle.class),
    Modi(7,"modi","https://www.mdnice.com/","mdnice.com","^(http|https)://([\\w.\\/]+writing\\/)(\\w+)",3,"墨滴"),
    OsChina(8,"oschina","https://my.oschina.net/","oschina.net","^(http|https)://([\\w.\\/]+\\/)(\\d+)",3,"开源中国博客",OsChinaAnalysisArticle.class),
    CTO(9,"51CTO","https://blog.51cto.com/","51cto.com","^(http|https)://([\\w.\\/]+\\/)(\\d+)",3,"51CTO博客",CTOAnalysisArticle.class),
    PoJie(10,"52Pojie","https://www.52pojie.cn/","52pojie.cn","^(http|https)://([\\w.\\/]+thread-)([\\w-]+)(\\.html|\\.htm)$",3,"吾爱破解"),
    KanXue(11,"kanxue","https://bbs.pediy.com/","pediy.com","^(http|https)://([\\w.\\/]+thread-)(\\w+)(\\.html|\\.htm)$",3,"看雪安全论坛"),
    Stackoverflow(12,"stackoverflow","https://stackoverflow.com/","stackoverflow.com","",3,"栈溢出论坛"),
    InfoQ(13,"infoQ","https://xie.infoq.cn/","xie.infoq.cn","^(http|https)://([\\w.\\/]+article\\/)(\\w+)",3,"infoQ中文站博客",InfoQAnalysisArticle.class),
    TencentCloud(14,"TencentCloud","https://cloud.tencent.com/","cloud.tencent.com","^(http|https)://([\\w.\\/]+article\\/)(\\w+)",3,"腾讯云社区"),
    //Other(14,"other","","other","",3,"其他文章"),
    ZhiDeMai(15,"ZhiDeMai","https://post.smzdm.com/","post.smzdm.com","^(http|https)://([\\w.\\/]+p\\/)(\\w+)",3,"值得买社区",SmzdmAnalysisArticle.class),
    ;
    /**
     * typeId
     */
    private int id;
    /**
     * 名称
     */
    private String name;
    /**
     * 常见地址
     */
    private String url;
    /**
     * 域名
     */
    private String domain;


    /**
     * 正则获取id，实际以爬虫配置表为准
     */
    private String regStr;

    private int group;

    /**
     * 描述
     */
    private String desc;

    /**
     * 服务类名称
     */
    private Class<AnalysisArticleStrategy> clazz;

    SourceType(int id, String name, String url, String domain, String regStr, int group, String desc) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.domain = domain;
        this.regStr = regStr;
        this.group = group;
        this.desc = desc;
    }

    SourceType(int id, String name, String url, String domain, String regStr, int group, String desc, Class clazz) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.domain = domain;
        this.regStr = regStr;
        this.group = group;
        this.desc = desc;
        this.clazz = clazz;
    }

    public static Optional<SourceType> match(String fullUrl){
        return Arrays.asList(SourceType.values()).stream().filter(s ->
                fullUrl.contains(s.getDomain())).findFirst();
    }

    public Class<AnalysisArticleStrategy> getClazz() {
        return clazz;
    }

    public int getGroup() {
        return group;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return domain;
    }

    public String getRegStr() {
        return regStr;
    }

    public String getDesc() {
        return desc;
    }
}
