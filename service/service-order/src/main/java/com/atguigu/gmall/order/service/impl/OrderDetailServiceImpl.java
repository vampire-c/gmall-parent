package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
 * @createDate 2022-10-23 20:47:27
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 获取购买商品明细
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public List<OrderDetail> getOrderDetails(Long orderId, Long userId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orderId)
                .eq(OrderDetail::getUserId, userId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(queryWrapper);
        return orderDetailList;
    }
}




