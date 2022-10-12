package com.atguigu.gmall.product;

import com.atguigu.gmall.cache.MallCacheAutoConfiguration;
import com.atguigu.gmall.common.config.minio.annotation.EnableMinio;
import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import com.atguigu.gmall.common.exception.EnableAutoExceptionHandle;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// @EnableCircuitBreaker // 熔断降级
// @EnableDiscoveryClient // 服务发现
// @SpringBootApplication

@EnableAutoExceptionHandle
@EnableTransactionManagement // 开启事务
@EnableSwagger3 // 开启Swagger
@EnableMinio //开启文件上传
@MapperScan("com.atguigu.gmall.product.mapper")
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}

