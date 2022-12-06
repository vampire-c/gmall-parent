package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/inner/order")
@FeignClient("service-order")
public interface OrderFeignClient {


    /**
     * 获取订单数据
     *
     * @return
     */
    @GetMapping("/orderConfirm")
    public Result<OrderConfirmVo> getOrderConfirmData();


    /**
     * 根据订单id和用户id查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/orderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfoByOrderId(@PathVariable Long orderId);

}
