package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.entity.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/inner/user")
@FeignClient("service-user")
public interface UserFeignClient {


    /**
     * 获取用户收货地址
     *
     * @return
     */
    @GetMapping("/userAddressList")
    public Result<List<UserAddress>> getUserAddressList();
}
