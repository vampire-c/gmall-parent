package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.minio.annotation.EnableMinio;
import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

// @EnableCircuitBreaker // 熔断降级
// @EnableDiscoveryClient // 服务发现
// @SpringBootApplication

@EnableSwagger3
@EnableMinio
@MapperScan("com.atguigu.gmall.product.mapper")
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}

