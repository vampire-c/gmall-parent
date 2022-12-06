package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.ware.WareStockResultMsg;

import java.util.List;

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

    /**
     * 关闭订单
     *
     * @param orderId
     */
    void closeOrder(Long orderId, Long userId);

    /**
     * 修改订单状态
     *
     * @param expectOrderStatusList   期望订单状态集合(满足任一即可)
     * @param expectProcessStatusList 期望订单处理状态集合(满足任一即可)
     * @param orderStatus             修改成的订单状态
     * @param processStatus           修改成的订单处理状态
     * @param orderId                 订单id
     * @param userId                  用户id
     */
    void changeOrderStatusByCAS(List<OrderStatus> expectOrderStatusList,
                                List<ProcessStatus> expectProcessStatusList,
                                OrderStatus orderStatus,
                                ProcessStatus processStatus,
                                Long orderId,
                                Long userId);

    /**
     * 根据订单id和用户id查询订单详情
     *
     * @param orderId
     * @param userId
     * @return
     */
    OrderInfo getOrderInfoByOrderIdAndUserId(Long orderId, Long userId);

    /**
     * 修改订单为已支付
     *
     * @param json
     */
    void updateOrderStatusPayed(String json);


    /**
     * 感知库存扣减, 修改订单状态
     *
     * @param wareStockResultMsg
     */
    void updateOrderStatusByStockResult(WareStockResultMsg wareStockResultMsg);
}
