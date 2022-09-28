package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.config.minio.service.FileUploadService;
import com.atguigu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件接口")
@RequestMapping("/admin/product")
@RestController
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    @ApiOperation("文件上传")
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart MultipartFile file) throws Exception {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        String url = this.fileUploadService.upload(file);
        return Result.ok(url);
    }
}
