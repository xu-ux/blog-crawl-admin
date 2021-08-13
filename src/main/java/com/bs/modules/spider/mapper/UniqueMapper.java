package com.bs.modules.spider.mapper;

import com.bs.modules.spider.domain.Unique;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.MyMapper;

@Mapper
public interface UniqueMapper extends MyMapper<Unique> {
}