package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

@SpringBootTest
public class MinioTest {


    @Test
    public void uploadTest() throws Exception {
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient
                    ("http://192.168.179.180:9000",
                            "admin",
                            "admin123456");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("mall");
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 不存在则创建
                minioClient.makeBucket("mall");
            }

            // 使用putObject上传一个文件到存储桶中。

            FileInputStream fileInputStream = new FileInputStream("F:\\1.png");
            PutObjectOptions putObjectOptions = new PutObjectOptions(fileInputStream.available(), -1);
            minioClient.putObject("mall", "1.png", fileInputStream, putObjectOptions);
            System.out.println("上传成功");
        } catch (MinioException e) {
            System.err.println("上传失败" + e);
        }


    }

}
