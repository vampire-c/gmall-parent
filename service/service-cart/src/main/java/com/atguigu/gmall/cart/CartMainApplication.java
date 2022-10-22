package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.config.pool.annotation.EnableAppThreadPool;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAppThreadPool
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@EnableUserAuthFeignInterceptor
@EnableAutoExceptionHandle
@SpringCloudApplication
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class, args);
    }
}
