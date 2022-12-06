package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import com.atguigu.gmall.util.annotation.EnableMallRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableMallRabbit
@EnableAutoExceptionHandle // 全局异常
@EnableUserAuthFeignInterceptor // 统一鉴权
@EnableFeignClients("com.atguigu.gmall.feign.order")
@SpringCloudApplication
public class PayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class, args);
    }
}
