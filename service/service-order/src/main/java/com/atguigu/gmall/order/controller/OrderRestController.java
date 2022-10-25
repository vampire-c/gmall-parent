package com.atguigu.gmall.order.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {

    @Autowired
    private OrderBizService orderBizService;

    /**
     * 提交订单
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @Valid
                              // 校验 OrderSubmitVo
                              @RequestBody OrderSubmitVo orderSubmitVo) {
        Long orderId = orderBizService.submitOrder(tradeNo, orderSubmitVo);
        return Result.ok(orderId.toString());
    }
}
