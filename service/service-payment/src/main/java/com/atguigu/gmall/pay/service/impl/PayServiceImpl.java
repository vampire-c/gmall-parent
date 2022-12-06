package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.pay.config.properties.AlipayProperties;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private OrderFeignClient orderFeignClient;


    /**
     * 为订单生成支付页
     *
     * @param orderId
     * @return
     */
    @Override
    public String generateOrderPayPage(Long orderId) throws AlipayApiException {
        // 创建一个 AlipayClient

        // 创建一个支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayProperties.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());

        // 获取订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfoByOrderId(orderId).getData();

        //设置此次交易的内容
        Map<String, String> bizContent = new HashMap<>();
        // 订单交易编号
        bizContent.put("out_trade_no", orderInfo.getOutTradeNo());
        // 此次订单总金额
        bizContent.put("total_amount", orderInfo.getTotalAmount().toString());
        // 订单标题
        bizContent.put("subject", orderInfo.getTradeBody());
        // 扩展参数
        // bizContent.put("business_params","{"+orderInfo.getUserId()+"}");
        //
        String expireTime = DateUtil.formatDate(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss");
        bizContent.put("time_expire", expireTime);

        // 销售产品码，与支付宝签约的产品码名称。注：目前电脑支付场景下仅支持FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(Jsons.toString(bizContent));

        // 执行支付请求
        return alipayClient.pageExecute(alipayRequest).getBody();
    }
}
