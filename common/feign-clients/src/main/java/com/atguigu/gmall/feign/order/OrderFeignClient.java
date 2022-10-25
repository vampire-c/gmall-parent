package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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

}
