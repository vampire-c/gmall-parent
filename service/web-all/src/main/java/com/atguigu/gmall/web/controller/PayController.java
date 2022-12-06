package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayController {

    @Autowired
    private OrderFeignClient orderFeignClient;


    /**
     * 支付确认页
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId, Model model) {
        // 远程调用获取订单详情
        OrderInfo orderInfo = orderFeignClient.getOrderInfoByOrderId(orderId).getData();
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }

    /**
     * 支付成功页
     *
     * @return
     */
    @GetMapping("/pay/success.html")
    public String paySuccess() {
        return "payment/success";
    }
}
