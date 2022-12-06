package com.atguigu.gmall.order;

import com.atguigu.gmall.common.config.pool.annotation.EnableAppThreadPool;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import com.atguigu.gmall.feign.ware.fallback.WareFeignClientFallback;
import com.atguigu.gmall.util.annotation.EnableMallRabbit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableMallRabbit
@Import(WareFeignClientFallback.class)
@EnableAppThreadPool
@EnableAutoExceptionHandle
@EnableUserAuthFeignInterceptor // 启动拦截器,使id透传
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.user",
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.ware"

})
@MapperScan(basePackages = "com.atguigu.gmall.order.mapper")
@SpringCloudApplication
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class, args);
    }
}
