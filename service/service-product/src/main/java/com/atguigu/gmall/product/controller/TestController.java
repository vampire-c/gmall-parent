package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin/product")
@RestController
public class TestController {


    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.ok("hello");
    }

}
