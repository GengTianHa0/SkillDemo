package com.hao.babytun.mapper;

import com.hao.babytun.entity.TCategory;

public interface TCategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(TCategory record);

    int insertSelective(TCategory record);

    TCategory selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(TCategory record);

    int updateByPrimaryKey(TCategory record);
}