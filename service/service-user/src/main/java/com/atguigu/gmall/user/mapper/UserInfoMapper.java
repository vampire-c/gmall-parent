package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.user.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Anonymous
* @description 针对表【user_info(用户表)】的数据库操作Mapper
* @createDate 2022-10-19 09:13:19
* @Entity com.atguigu.gmall.user.entity.UserInfo
*/
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}




