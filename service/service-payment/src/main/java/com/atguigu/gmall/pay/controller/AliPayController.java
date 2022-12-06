package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment/alipay")
public class AliPayController {

    @Autowired
    private PayService payService;

    /**
     * 跳转到支付页
     *
     * @param orderId
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/submit/{orderId}", produces = MediaType.TEXT_HTML_VALUE)
    public String paySubmitPage(@PathVariable Long orderId) throws AlipayApiException {
        return payService.generateOrderPayPage(orderId);
    }
}
