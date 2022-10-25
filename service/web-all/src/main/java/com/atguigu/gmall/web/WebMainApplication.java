package com.atguigu.gmall.web;

import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// @SpringCloudApplication
@EnableSwagger3
@EnableAutoExceptionHandle
@EnableUserAuthFeignInterceptor // 启动拦截器,使id透传
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
public class WebMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebMainApplication.class, args);
    }
}
