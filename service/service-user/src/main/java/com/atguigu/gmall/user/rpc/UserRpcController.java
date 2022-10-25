package com.atguigu.gmall.user.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/inner/user")
@RestController
public class UserRpcController {


    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户收货地址
     *
     * @return
     */
    @GetMapping("/userAddressList")
    public Result<List<UserAddress>> getUserAddressList() {
        List<UserAddress> userAddressList = userAddressService.getUserAddressListByUserId();
        return Result.ok(userAddressList);
    }
}
