package com.hao.babytun.mapper;

import com.hao.babytun.entity.TOrder;

public interface TOrderMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(TOrder record);

    int insertSelective(TOrder record);

    TOrder selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(TOrder record);

    int updateByPrimaryKey(TOrder record);

    TOrder findByOrderNo(String orderNo);
}