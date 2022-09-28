package com.atguigu.gmall.common.config.minio.service.impl;

import com.atguigu.gmall.common.config.minio.config.MinioProperties;
import com.atguigu.gmall.common.config.minio.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioClient minioClient;

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


    /**
     * 文件上传到Minio
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) throws Exception {
        // 唯一文件名
        String filename = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        PutObjectOptions putObjectOptions = new PutObjectOptions(file.getSize(), -1);

        // 创建MinioClient
        // MinioClient client = new MinioClient(endpoint, accessKey, secretKey);
        // 判断桶是否存在
        // boolean mall = minioClient.bucketExists(bucketName);
        // if (!mall) {
        //     minioClient.makeBucket(bucketName);
        // }

        // 上传文件
        minioClient.putObject("mall", filename, inputStream, putObjectOptions);

        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + filename;
        return url;
    }
}
