package com.atguigu.gmall.common.config.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {

    String endpoint;

    String accessKey;

    String secretKey;

    String bucketName;

}
