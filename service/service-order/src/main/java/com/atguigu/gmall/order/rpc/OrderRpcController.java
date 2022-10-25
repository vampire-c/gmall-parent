package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/inner/order")
public class OrderRpcController {

    @Autowired
    private OrderBizService orderBizService;

    /**
     * 获取订单结算数据
     *
     * @return
     */
    @GetMapping("/orderConfirm")
    public Result<OrderConfirmVo> getOrderConfirmData() {
        OrderConfirmVo orderConfirmVo = orderBizService.getOrderConfirmData();
        return Result.ok(orderConfirmVo);
    }
}
