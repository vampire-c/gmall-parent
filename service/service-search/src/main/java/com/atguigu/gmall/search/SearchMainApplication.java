package com.atguigu.gmall.search;


import com.atguigu.gmall.common.config.swagger.annotation.EnableSwagger3;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


@EnableSwagger3
@SpringCloudApplication
public class SearchMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchMainApplication.class, args);
    }
}
