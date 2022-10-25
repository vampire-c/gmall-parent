package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【user_address(用户地址表)】的数据库操作Service实现
 * @createDate 2022-10-19 09:13:19
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 获取用户收货地址
     *
     * @return
     */
    @Override
    public List<UserAddress> getUserAddressListByUserId() {
        // 获取用户id
        Long userInfoId = UserAuthUtils.getUserAuthInfo().getUserInfoId();
        // 查询条件 用户id相等
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userInfoId);
        List<UserAddress> userAddressList = userAddressMapper.selectList(queryWrapper);
        return userAddressList;
    }
}




