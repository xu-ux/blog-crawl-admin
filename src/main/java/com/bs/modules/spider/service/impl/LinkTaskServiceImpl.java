package com.bs.modules.spider.service.impl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.io.IoUtil;
import com.bs.common.exception.base.CommonException;
import com.bs.common.tools.string.StringUtil;
import com.bs.modules.spider.enums.OriginalType;
import com.bs.modules.spider.enums.RunStatus;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.pojo.vo.LinkTaskVO;
import com.bs.modules.spider.service.IAnalysisArticleService;
import com.bs.modules.spider.service.IAnalysisUrlService;
import com.bs.modules.spider.service.ILocalFileService;
import com.bs.modules.spider.util.ArticleUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.bs.common.web.domain.request.PageDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bs.modules.spider.mapper.LinkTaskMapper;
import com.bs.modules.spider.domain.LinkTask;
import com.bs.modules.spider.service.ILinkTaskService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

/**
 * 标签管理Service业务层处理
 *
 * @author xucl
 * @date 2021-08-05
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class LinkTaskServiceImpl implements ILinkTaskService {


    @Autowired
    private LinkTaskMapper linkTaskMapper;


    @Autowired
    private ILocalFileService localFileService;

    @Autowired
    private IAnalysisUrlService analysisUrlService;

    @Autowired
    private IAnalysisArticleService analysisArticleService;

    /**
     * 查询标签管理
     *
     * @param id 标签管理ID
     * @return 标签管理
     */
    @Override
    public LinkTask selectLinkTaskById(Integer id) {
        return linkTaskMapper.selectLinkTaskById(id);
    }

    /**
     * 查询标签管理列表
     *
     * @param linkTask 标签管理
     * @return 标签管理
     */
    @Override
    public List<LinkTask> selectLinkTaskList(LinkTask linkTask) {
        return linkTaskMapper.selectLinkTaskList(linkTask);
    }

    /**
     * 查询标签管理
     *
     * @param linkTask   标签管理
     * @param pageDomain
     * @return 标签管理 分页集合
     */
    @Override
    public PageInfo<LinkTaskVO> selectLinkTaskPage(LinkTask linkTask, PageDomain pageDomain) {
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getLimit());
        List<LinkTask> data = linkTaskMapper.selectLinkTaskList(linkTask);
        PageInfo<LinkTask> pageInfo = new PageInfo<>(data);

        List<LinkTaskVO> collect = data.stream().map(s -> {
            LinkTaskVO vo = new LinkTaskVO();
            BeanUtils.copyProperties(s, vo);
            vo.setRunStatus(RunStatus.ofId(s.getRunStatus()).getName());
            vo.setOriginalType(OriginalType.ofId(s.getOriginalType()).getName());
            return vo;
        }).collect(Collectors.toList());

        PageInfo<LinkTaskVO> voPageInfo = new PageInfo<>(collect);
        voPageInfo.setTotal(pageInfo.getTotal());
        return voPageInfo;
    }

    /**
     * 新增标签管理
     *
     * @param linkTask 标签管理
     * @return 结果
     */

    @Override
    public int insertLinkTask(LinkTask linkTask) {
        Optional<ArticleUrlInfo> urlInfo = analysisUrlService.parseUrlSecond(linkTask.getLinkUrl());
        if (!urlInfo.isPresent()){
            throw new CommonException("请输入有效的地址");
        }
        linkTask.setCreateTime(new Date());
        linkTask.setRunStatus((short)-1);
        linkTask.setOriginalType(urlInfo.get().getOriginalType());
        return linkTaskMapper.insertLinkTask(linkTask);
    }

    /**
     * 修改标签管理
     *
     * @param linkTask 标签管理
     * @return 结果
     */
    @Override
    public int updateLinkTask(LinkTask linkTask) {
        return linkTaskMapper.updateByPrimaryKeySelective(linkTask);
    }

    /**
     * 删除标签管理对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteLinkTaskByIds(String[] ids) {
        return linkTaskMapper.deleteLinkTaskByIds(ids);
    }

    /**
     * 删除标签管理信息
     *
     * @param id 标签管理ID
     * @return 结果
     */
    @Override
    public int deleteLinkTaskById(Integer id) {
        return linkTaskMapper.deleteLinkTaskById(id);
    }

    /**
     * 解析谷歌浏览器导出的标签文件
     *
     * @param file
     */
    @Override
    public void analysisChromeFile(MultipartFile file) throws CommonException {
        String url = localFileService.uploadTagFile(file);

        try {
            InputStreamReader reader = new InputStreamReader(file.getInputStream(), Charset.defaultCharset());
            String s = IoUtil.read(reader);
            Document document = Jsoup.parse(s);
            Elements elements = document.select("a");
            if (CollectionUtils.isEmpty(elements)){
                throw new CommonException("请检查上传文件是否合法！");
            }
            elements.stream().forEach(el -> {
                final String href = el.attr("href");
                final String text = el.text();
                Optional<ArticleUrlInfo> urlInfo = analysisUrlService.parseUrlSecond(href);

                if (urlInfo.isPresent()){
                    LinkTask linkTask = new LinkTask()
                            .setLinkTitle(text)
                            .setLinkUrl(urlInfo.get().getOriginalUrl())
                            .setFileKey(url)
                            .setCreateTime(new Date())
                            .setRunStatus((short)-1)
                            .setOriginalType(urlInfo.get().getOriginalType());
                    linkTaskMapper.insert(linkTask);
                }
            });
        } catch (Exception e) {
            log.error("解析谷歌浏览器导出的标签文件异常 fileName:{}",file.getOriginalFilename(),e);
            throw new CommonException("请检查上传文件是否合法！");
        }
    }

    /**
     * 批量爬取链接地址
     *
     * @param ida
     * @return
     */
    @Override
    public int crawlLinkTaskByIds(String ida) {
        List<LinkTask> tasks = linkTaskMapper.selectByIds(ida);
        if (CollectionUtils.isEmpty(tasks)){
            throw new CommonException("链接信息不存在");
        }
        tasks.stream().filter(Objects::nonNull)
                .forEach(
                task -> {
                    analysisArticleService.crawlerArticle(task);
                }
        );

        return 1;
    }


}
