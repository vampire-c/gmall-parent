package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Api(tags = "订单接口")
@Controller
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 跳转到订单结算
     *
     * @return
     */
    @ApiOperation("跳转订单结算页")
    @GetMapping("/trade.html")
    public String orderConfirmPage(Model model) {
        OrderConfirmVo orderConfirmVo = orderFeignClient.getOrderConfirmData().getData();

        model.addAttribute("detailArrayList", orderConfirmVo.getDetailArrayList());
        model.addAttribute("totalNum", orderConfirmVo.getTotalNum());
        model.addAttribute("totalAmount", orderConfirmVo.getTotalAmount());
        model.addAttribute("userAddressList", orderConfirmVo.getUserAddressList());
        model.addAttribute("tradeNo", orderConfirmVo.getTradeNo());

        return "order/trade";
    }
}
