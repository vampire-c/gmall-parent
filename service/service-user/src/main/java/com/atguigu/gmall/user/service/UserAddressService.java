package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.entity.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【user_address(用户地址表)】的数据库操作Service
 * @createDate 2022-10-19 09:13:19
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户收货地址
     *
     * @return
     */
    List<UserAddress> getUserAddressListByUserId();
}
