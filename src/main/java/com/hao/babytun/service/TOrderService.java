package com.hao.babytun.service;

import com.hao.babytun.entity.TOrder;
import com.hao.babytun.mapper.TOrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 谷鑫 G x
 * @Classname TOrderService
 * @Describe:
 * @date 2018/11/9 14:56
 */
@Service
public class TOrderService {
    @Resource
    private TOrderMapper tOrderMapper;

    public TOrder findByOrderNo(String orderNo) {
        return tOrderMapper.findByOrderNo(orderNo);
    }
}
