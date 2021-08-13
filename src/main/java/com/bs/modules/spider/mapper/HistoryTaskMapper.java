package com.bs.modules.spider.mapper;

import com.bs.modules.spider.domain.HistoryTask;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.MyMapper;

@Mapper
public interface HistoryTaskMapper extends MyMapper<HistoryTask> {
}