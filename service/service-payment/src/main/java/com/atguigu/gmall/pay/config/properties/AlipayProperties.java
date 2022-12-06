package com.atguigu.gmall.pay.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {

    // 应用id
    private String appId;
    // 商户私钥
    private String merchantPrivateKey;
    // 支付宝公钥
    private String alipayPublicKey;
    // 签名方式 RSA
    private String signType;
    // 字符集 UTF-8
    private String charset;
    // 支付网关
    private String gatewayUrl;
    // 格式化方式  "json"
    private String format;

    // 同步跳转页, 当浏览器支付成功后要跳转的位置
    private String returnUrl;
    // 异步通知, 当支付成功后的通知信息
    private String notifyUrl;


}
