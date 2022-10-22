package com.atguigu.gmall.web;

import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// @SpringCloudApplication
@EnableUserAuthFeignInterceptor
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign")
public class WebMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebMainApplication.class, args);
    }
}
