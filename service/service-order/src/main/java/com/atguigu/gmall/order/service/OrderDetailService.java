package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.entity.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【order_detail(订单明细表)】的数据库操作Service
 * @createDate 2022-10-23 20:47:27
 */
public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 获取购买商品明细
     *
     * @param orderId
     * @param userId
     * @return
     */
    List<OrderDetail> getOrderDetails(Long orderId, Long userId);

}
