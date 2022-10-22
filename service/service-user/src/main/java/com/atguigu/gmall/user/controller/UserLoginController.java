package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import com.atguigu.gmall.user.vo.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户接口")
@RequestMapping("/api/user")
@RestController
public class UserLoginController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户登录
     *
     * @param userLoginVo
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserLoginVo userLoginVo) {
        UserLoginSuccessVo userLoginSuccessVo = userInfoService.login(userLoginVo);
        return Result.ok(userLoginSuccessVo);
    }


    /**
     * 登录
     *
     * @return
     */
    @ApiOperation("退出登录")
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token) {
        userInfoService.logout(token);
        return Result.ok();
    }
}
