package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.config.properties.AlipayProperties;
import com.atguigu.gmall.util.consts.MqConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/api/payment/alipay")
@RestController
public class OrderPayedController {

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 支付成功通知
     *
     * @param params
     * @return
     * @throws AlipayApiException
     */
    @PostMapping("/payed/success")
    public String listenPayed(@RequestParam Map<String, String> params) throws AlipayApiException {

        // 验签
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getCharset(),
                alipayProperties.getSignType());
        if (signVerified) {
            log.info("支付成功通知...{}", Jsons.toString(params));
            log.info("验签通过");

            // 修改订单状态(发送MQ消息)
            rabbitTemplate.convertAndSend(
                    MqConst.ORDER_EVENT_EXCHANGE,
                    MqConst.RK_ORDER_PAYED,
                    Jsons.toString(params));
            log.info("修改订单状态为已支付");
            return "success";
        }

        log.info("验签失败");
        return "error";
    }

    @GetMapping("/payed/success")
    public String listen() {
        return "哈哈";
    }
}
