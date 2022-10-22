package com.atguigu.gmall.user;

import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableAutoExceptionHandle
@EnableSwagger3
@MapperScan(basePackages = "com.atguigu.gmall.user.mapper")
@SpringCloudApplication
public class UserMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMainApplication.class, args);
    }
}
