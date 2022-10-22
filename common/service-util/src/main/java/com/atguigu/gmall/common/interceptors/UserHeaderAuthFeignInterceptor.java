package com.atguigu.gmall.common.interceptors;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.user.vo.UserAuthInfoVo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class UserHeaderAuthFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // log.info("user信息 feign拦截器");
        // 1 从map中获取request
        // HttpServletRequest request = CartController.requestMap.get(Thread.currentThread());
        // 2 从共享线程中获取request
        // HttpServletRequest request = CartController.threadLocal.get();
        // 3 从springMVC容器中获取request
        /*
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userInfoId = request.getHeader(RedisConst.USER_ID_HEADER);
        String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
        */
        UserAuthInfoVo userAuthInfoVo = UserAuthUtils.getUserAuthInfo();
        Long userInfoId = userAuthInfoVo.getUserInfoId();
        String userTempId = userAuthInfoVo.getUserTempId();

        if (!StringUtils.isEmpty(userInfoId)) {
            template.header(RedisConst.USER_ID_HEADER, userInfoId.toString());
        }
        template.header(RedisConst.USER_TEMP_ID_HEADER, userTempId);

        // log.info("");
    }
}
