package com.atguigu.gmall.common.util;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.user.vo.UserAuthInfoVo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息工具类
 */
public class UserAuthUtils {
    public static UserAuthInfoVo getUserAuthInfo() {
        // 根据线程绑定机制,和 spring会自动把当前正在处理的请求共享到当前线程 获取 userInfoId, userTempId
        // 从同一个线程绑定中获取旧请求 (的userInfoId和userTempId)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!StringUtils.isEmpty(attributes)) {

            HttpServletRequest request = attributes.getRequest();
            String userInfoId = request.getHeader(RedisConst.USER_ID_HEADER);
            String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
            // String userInfoId = null;
            try {
                if (!StringUtils.isEmpty(userInfoId)) {
                    return new UserAuthInfoVo(Long.parseLong(userInfoId), userTempId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return new UserAuthInfoVo(null, userTempId);
        }
        return null;
    }

}
