package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

public interface PayService {

    /**
     * 为订单生成支付页
     *
     * @param orderId
     * @return
     */
    String generateOrderPayPage(Long orderId) throws AlipayApiException;
}
