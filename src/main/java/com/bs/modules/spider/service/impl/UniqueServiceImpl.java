package com.bs.modules.spider.service.impl;

import com.bs.modules.spider.domain.Unique;
import com.bs.modules.spider.mapper.UniqueMapper;
import com.bs.modules.spider.pojo.dto.ArticleUrlInfo;
import com.bs.modules.spider.service.IUniqueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.nntp.ArticleInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName UniqueServiceImpl
 * @Description
 * @date 2021/8/3
 */
@Slf4j
@Service
public class UniqueServiceImpl implements IUniqueService {

    @Autowired
    private UniqueMapper uniqueMapper;


    /**
     * 是否已经存在该原始地址，不存在则新增
     *
     * @param articleUrlInfo
     * @return
     */
    @Override
    public boolean existArticleUrlInfo(ArticleUrlInfo articleUrlInfo) {
        try {
            Unique select = new Unique().setOriginalId(articleUrlInfo.getOriginalId())
                    .setOriginalType(articleUrlInfo.getOriginalType());
            int i = uniqueMapper.selectCount(select);
            if ( i <= 0){
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("是否已经存在该原始地址 异常：{}",e);
            return false;
        }
    }

    /**
     * 保存唯一值
     * @param articleUrlInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticleUrlInfo(ArticleUrlInfo articleUrlInfo){
        Unique saveData = new Unique();
        BeanUtils.copyProperties(articleUrlInfo,saveData,"id");
        saveData.setCreateTime(new Date());
        uniqueMapper.insert(saveData);
    }

    /**
     * 删除唯一文章信息
     *
     * @param originalId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleUnique(String originalId) {
        Example example = Example.builder(Unique.class).where(WeekendSqls.<Unique>custom()
                .andEqualTo(Unique::getOriginalId,originalId)).build();
        uniqueMapper.deleteByExample(example);
    }
}
