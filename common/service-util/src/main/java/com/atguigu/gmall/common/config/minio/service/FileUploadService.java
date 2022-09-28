package com.atguigu.gmall.common.config.minio.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {


    /**
     * 文件上传到Minio
     *
     * @param file
     * @return
     */
    String upload(MultipartFile file) throws Exception;
}
