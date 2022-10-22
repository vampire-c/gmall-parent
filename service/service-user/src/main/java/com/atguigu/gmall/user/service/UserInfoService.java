package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import com.atguigu.gmall.user.vo.UserLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Anonymous
 * @description 针对表【user_info(用户表)】的数据库操作Service
 * @createDate 2022-10-19 09:13:19
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     *
     * @param userLoginVo
     * @return
     */
    UserLoginSuccessVo login(UserLoginVo userLoginVo);

    /**
     * 退出登录
     * @param token
     */
    void logout(String token);
}
