package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import com.atguigu.gmall.user.vo.UserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author Anonymous
 * @description 针对表【user_info(用户表)】的数据库操作Service实现
 * @createDate 2022-10-19 09:13:19
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     *
     * @param userLoginVo
     * @return
     */
    @Override
    public UserLoginSuccessVo login(UserLoginVo userLoginVo) {
        String passWd = MD5.encrypt(userLoginVo.getPasswd());

        // 查询用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getLoginName, userLoginVo.getLoginName())
                .eq(UserInfo::getPasswd, passWd);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        // 数据库不存在抛出异常
        if (StringUtils.isEmpty(userInfo)) {
            throw new GmallException(ResultCodeEnum.USER_PWD_INVAILD);
        }

        // 登录成功
        UserLoginSuccessVo userLoginSuccessVo = new UserLoginSuccessVo();
        String token = UUID.randomUUID().toString().replace("-", "");
        userLoginSuccessVo.setToken(token); // 用户令牌
        userLoginSuccessVo.setNickName(userInfo.getNickName()); // 用户昵称

        // redis共享登录信息 user:login:令牌=用户信息
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN + token, Jsons.toString(userInfo), RedisConst.USER_LOGIN_TTL, RedisConst.TTL_UNIT_DAYS);

        return userLoginSuccessVo;
    }

    /**
     * 退出登录
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(RedisConst.USER_LOGIN + token);
    }
}




