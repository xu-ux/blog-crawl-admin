package tk.mybatis.mapper;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.RowBoundsMapper;

/**
 * 通用tk-mapper
 */
public interface MyMapper<T> extends Mapper<T>, ConditionMapper<T>, IdsMapper<T>, InsertListMapper<T>, RowBoundsMapper<T> {



}