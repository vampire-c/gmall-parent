package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;

public interface OrderBizService {
    /**
     * 获取订单结算数据
     *
     * @return
     */
    OrderConfirmVo getOrderConfirmData();

    /**
     * 提交订单
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    Long submitOrder(String tradeNo, OrderSubmitVo orderSubmitVo);

    /**
     * 保存订单
     *
     * @param tradeNo
     * @param orderSubmitVo
     */
    Long saveOrder(String tradeNo, OrderSubmitVo orderSubmitVo);

}
