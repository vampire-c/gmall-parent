package com.atguigu.gmall.common.config.minio.config;

import com.atguigu.gmall.common.config.minio.service.FileUploadService;
import com.atguigu.gmall.common.config.minio.service.impl.FileUploadServiceImpl;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(MinioProperties.class)
@Configuration
public class MinioAutoConfiguration {

    @Autowired
    private MinioProperties minioProperties;

    // @Value("${app.minio.endpoint}")
    // String endpoint;
    // @Value("${app.minio.accessKey}")
    // String accessKey;
    // @Value("${app.minio.secretKey}")
    // String secretKey;
    // @Value("${app.minio.bucketName}")
    // String bucketName;

    @Bean
    public MinioClient minioClient() throws Exception {
        // 创建MinioClient
        MinioClient minioClient = new MinioClient
                (minioProperties.getEndpoint(),
                        minioProperties.getAccessKey(),
                        minioProperties.getSecretKey());
        // 判断桶是否存在
        boolean mall = minioClient.bucketExists(minioProperties.getBucketName());
        if (!mall) {
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }

    @Bean
    public FileUploadService fileUploadService() {
        return new FileUploadServiceImpl();
    }
}
