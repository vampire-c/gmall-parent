package com.atguigu.gmall.item;

import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableAutoExceptionHandle
@EnableSwagger3 // 开启Swagger
@EnableFeignClients
@SpringCloudApplication
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class, args);
    }
}
