package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserIdTestController {
    @GetMapping("/search/test/abc")
    public Result test(@RequestHeader(RedisConst.USER_ID_HEADER) Long userInfoId) {
        return Result.ok(userInfoId);
    }
}
