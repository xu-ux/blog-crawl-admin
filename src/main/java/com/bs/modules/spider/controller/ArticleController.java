package com.bs.spider.controller;

import com.alibaba.fastjson.JSONObject;
import com.bs.common.tools.common.BeanHelper;
import com.bs.modules.spider.domain.Article;
import com.bs.modules.spider.pojo.vo.ArticleCategoryVO;
import com.bs.modules.spider.pojo.vo.ArticleInfoVO;
import com.bs.modules.spider.pojo.vo.ArticleMdVO;
import com.bs.modules.spider.pojo.vo.ArticleVO;
import com.bs.modules.spider.service.IAnalysisArticleService;
import com.bs.modules.spider.service.IArticleService;
import com.bs.modules.spider.service.ILocalFileService;
import com.bs.modules.sys.domain.SysUser;
import com.github.pagehelper.PageInfo;
import com.bs.common.tools.string.Convert;
import com.bs.common.web.base.BaseController;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.common.web.domain.response.Result;
import com.bs.common.web.domain.response.module.ResultTable;
import com.bs.common.tools.secure.SecurityUtil;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章Controller
 *
 * @author xucl
 * @date 2021-08-03
 */
@RestController
@Api(tags = {"文章管理"})
@RequestMapping("/spider/article")
public class ArticleController extends BaseController {



    private String prefix = "spider/article";

    @Autowired
    private IArticleService articleService;


    @Autowired
    private IAnalysisArticleService analysisArticleService;

    @Autowired
    private ILocalFileService localFileService;

    @GetMapping("/main")
    @PreAuthorize("hasPermission('/spider/article/main','spider:article:main')")
    public ModelAndView main() {
        return jumpPage(prefix + "/main");
    }

    /**
     * 查询文章列表
     */
    @ResponseBody
    @GetMapping("/data")
    @PreAuthorize("hasPermission('/spider/article/data','spider:article:data')")
    public ResultTable list(@ModelAttribute Article article, PageDomain pageDomain) {
        PageInfo<ArticleVO> pageInfo = articleService.selectArticlePage(article, pageDomain);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 新增文章
     */
    @GetMapping("/add")
    @PreAuthorize("hasPermission('/spider/article/add','spider:article:add')")
    public ModelAndView add() {
        return jumpPage(prefix + "/add");
    }


    /**
     * 新增文章
     */
    @GetMapping("/addCategory")
    @PreAuthorize("hasPermission('/spider/article/add','spider:article:add')")
    public ModelAndView addCategory() {
        return jumpPage(prefix + "/addCategory");
    }


    /**
     * 修改文章
     */
    @GetMapping("/edit")
    @PreAuthorize("hasPermission('/spider/article/edit','spider:article:edit')")
    public ModelAndView edit(Long articleId, ModelMap mmap) {
        Article article = articleService.selectArticleById(articleId);
        mmap.put("article", article);
        return jumpPage(prefix + "/edit");
    }

    /**
     * 修改文章
     */
    @GetMapping("/preview")
    @PreAuthorize("hasPermission('/spider/article/preview','spider:article:edit')")
    public ModelAndView preview(Long articleId, ModelMap mmap) {
        Article article = articleService.selectArticleById(articleId);
        mmap.put("mdContent", article.getMdContent());
        mmap.put("articleId", article.getArticleId());
        return jumpPage(prefix + "/preview");
    }

    /**
     * 新增爬取文章
     */
    @ResponseBody
    @PostMapping("/crawl")
    @PreAuthorize("hasPermission('/spider/article/add','spider:article:add')")
    public Result save(@RequestBody Article article) {
        try {
            if (StringUtils.isBlank(article.getOriginalUrl())){
                return Result.failure("地址参数不能为空");
            }
            analysisArticleService.crawlerArticle(article.getOriginalUrl());
            return Result.success("提交爬取中");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    /**
     * 新增整个分类的爬取文章
     */
    @ResponseBody
    @PostMapping("/crawl/category")
    @PreAuthorize("hasPermission('/spider/article/add','spider:article:add')")
    public Result crawlCategory(@RequestBody ArticleCategoryVO vo) {
        try {
            String categoryUrl = vo.getCategoryUrl();
            if (StringUtils.isBlank(categoryUrl)){
                return Result.failure("地址参数不能为空");
            }
            List<ArticleCategoryVO> vos = analysisArticleService.crawlerArticleCategory(categoryUrl);
            return Result.success(vos);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    /**
     * 修改保存文章
     */
    @ResponseBody
    @PutMapping("/update")
    @PreAuthorize("hasPermission('/spider/article/edit','spider:article:edit')")
    public Result update(@RequestBody ArticleMdVO articleMdVO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleMdVO,article);
        return decide(articleService.updateArticle(article));
    }


    /**
     * 修改保存文章
     */
    @ResponseBody
    @PutMapping("/update/info")
    @PreAuthorize("hasPermission('/spider/article/edit','spider:article:edit')")
    public Result update(@RequestBody ArticleInfoVO articleInfoVO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleInfoVO,article);
        return decide(articleService.updateArticle(article));
    }

    /**
     * 删除
     */
    @ResponseBody
    @DeleteMapping("/remove/{articleId}")
    @PreAuthorize("hasPermission('/spider/article/remove','spider:article:remove')")
    public Result remove(@PathVariable("articleId") Long articleId) {
        return decide(articleService.deleteArticleById(articleId));
    }

    /**
     * 批量删除文章
     */
    @ResponseBody
    @DeleteMapping( "/batchRemove")
    @PreAuthorize("hasPermission('/spider/article/remove','spider:article:remove')")
    public Result batchRemove(String ids)
    {
        return Result.success("暂不支持批量删除");
    }


    /**
     * 上传图片
     * @param image
     * @return
     */
    @ResponseBody
    @PostMapping("/uploadImage")
    public Object uploadImage(@RequestParam("editormd-image-file") MultipartFile image){
        JSONObject jsonObject = new JSONObject();
        if(image != null) {
            String path = localFileService.uploadImage(image);
            jsonObject.put("url", path);
            jsonObject.put("success", 1);
            jsonObject.put("message", "upload success!");
            return jsonObject;
        }
        jsonObject.put("success", 0);
        jsonObject.put("message", "upload error!");
        return jsonObject;
    }
}
