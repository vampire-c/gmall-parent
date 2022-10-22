package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关的全局的过滤器
 */
@Slf4j
@Component
public class UserAuthFilter implements GlobalFilter {

    @Autowired
    private AuthUrlProperties authUrlProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // log.info("请求路径:{}", path);

        // 放行静态资源
        // 遍历集合(无需验证资源) /js/**  /css/**  /img/**
        for (String url : authUrlProperties.getNoAuthUrl()) {
            // 判断当前请求是否为静态资源路径
            if (antPathMatcher.match(url, path)) {
                return chain.filter(exchange);
            }
        }

        // 浏览器不可访问资源 /api/inner/**
        for (String url : authUrlProperties.getDenyUrl()) {
            if (antPathMatcher.match(url, path)) {
                Result<String> result = Result.build("", ResultCodeEnum.PERMISSION);
                return responseJson(exchange, result);
            }
        }

        // 需要验证资源
        for (String url : authUrlProperties.getLoginAuthUrl()) {
            if (antPathMatcher.match(url, path)) {
                // 获取token
                String token = getUserToken(exchange);
                // 不为空, 说明 有登录token
                if (!StringUtils.isEmpty(token)) {
                    // 判断令牌真伪
                    UserInfo userInfo = getUserInfoByToken(token);
                    if (StringUtils.isEmpty(userInfo)) {
                        // 伪造令牌, 返回到登录页
                        return locationToUrl(exchange, authUrlProperties.getLoginPage());
                    }
                    // 真实令牌,放行
                    return userIdThrought(exchange, chain, userInfo);
                } else {
                    // 没登录, 返回到登录页
                    return locationToUrl(exchange, authUrlProperties.getLoginPage());
                }
            }
        }


        // 普通请求
        String token = getUserToken(exchange);
        // 不为空, 说明 有登录token
        if (!StringUtils.isEmpty(token)) {
            // 判断令牌真伪
            UserInfo userInfo = getUserInfoByToken(token);
            if (StringUtils.isEmpty(userInfo)) {
                // 伪造令牌, 返回到登录页
                return locationToUrl(exchange, authUrlProperties.getLoginPage());
            } else {
                // 真实令牌,透传userId,放行
                return userIdThrought(exchange, chain, userInfo);
            }
        } else {
            // 没登录token,透传临时id,放行
            return userTempIdThrought(exchange, chain);

        }
    }

    /**
     * 普通请求没带token,没登录,,,,带了,透传临时id, 放行
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> userTempIdThrought(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tempId = getUserTempId(exchange);
        // 根据旧请求, 创建新请求, 并且添加请求头
        ServerHttpRequest newRequest = exchange.getRequest() // 获取请求
                .mutate() // 变异返回一个请求
                .header(RedisConst.USER_TEMP_ID_HEADER, tempId)
                .build();
        // 根据新请求和旧相应, 创建新的newExchange
        ServerWebExchange newExchange = exchange.mutate()
                .request(newRequest)
                .response(exchange.getResponse())
                .build();
        // 放行新的的newExchange
        return chain.filter(newExchange);
    }

    /**
     * 相应一个Json
     *
     * @param exchange
     * @param result
     * @return
     */
    private Mono<Void> responseJson(ServerWebExchange exchange, Result<String> result) {
        String jsonStr = Jsons.toString(result);
        // 获取相应数据
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        // 创建相应数据
        DataBuffer buffer = response.bufferFactory().wrap(jsonStr.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 透传用户id, 放行请求
     *
     * @param exchange
     * @param chain
     * @param userInfo
     * @return
     */
    private Mono<Void> userIdThrought(ServerWebExchange exchange, GatewayFilterChain chain, UserInfo userInfo) {
        // 添加用户id到请求头, 但是不允许修改
        // exchange.getRequest().getHeaders() // 获取到请求头
        //         .add(RedisConst.USER_ID_HEADER, String.valueOf(userInfo.getId())); // 添加请求头

        String tempId = getUserTempId(exchange);

        // 根据旧请求, 创建新请求, 并且添加请求头
        ServerHttpRequest newRequest = exchange.getRequest() // 获取请求
                .mutate() // 变异返回一个请求
                .header(RedisConst.USER_ID_HEADER, String.valueOf(userInfo.getId())) // 添加到请求头
                .header(RedisConst.USER_TEMP_ID_HEADER, tempId)
                .build();
        // 根据新请求和旧相应, 创建新的newExchange
        ServerWebExchange newExchange = exchange.mutate()
                .request(newRequest)
                .response(exchange.getResponse())
                .build();
        // 放行新的的newExchange
        return chain.filter(newExchange);
    }

    /**
     * 获取userTempId
     * @param exchange
     * @return
     */
    private String getUserTempId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String userTempId  = "";
        // 从cookie中获取token
        HttpCookie cookie = request.getCookies().getFirst("userTempId");
        if (!StringUtils.isEmpty(cookie)) {
            // 不为空,返回
            userTempId = cookie.getValue();
        } else {
            // 为空,从请求头中获取token
            userTempId = request.getHeaders().getFirst("userTempId");
        }
        return userTempId;
    }

    /**
     * 按照用户传来的令牌,到redis查询用户信息
     *
     * @param token
     * @return
     */
    private UserInfo getUserInfoByToken(String token) {
        String json = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN + token);
        if (!StringUtils.isEmpty(json)) {
            return Jsons.toObject(json, UserInfo.class);
        }
        return null;
    }

    /**
     * 重定向到登录页面
     *
     * @param exchange
     * @param loginPage
     * @return
     */
    private Mono<Void> locationToUrl(ServerWebExchange exchange, String loginPage) {
        // 获取response
        ServerHttpResponse response = exchange.getResponse();
        // 获取uri
        String uri = exchange.getRequest().getURI().toString();
        // 相应状态码
        response.setStatusCode(HttpStatus.FOUND);
        // 相应头 Location: http://passport.gmall.com/login.html?originUrl= 上一个页面地址(uri)
        response.getHeaders().add("Location", loginPage + "?originUrl=" + uri);
        // 清空假的token令牌, 创建并添加一个立即过期的cookie(token令牌)
        ResponseCookie token = ResponseCookie.from("token", "") // 创建cookie(token令牌)
                .domain(".gmall.com") // 域名范围内有效
                .maxAge(0) // 立即过期
                .build();
        response.addCookie(token); // 添加cookie,覆盖旧的错误token令牌
        return response.setComplete();
    }

    /**
     * 获取token
     *
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String userToken = "";
        // 从cookie中获取token
        HttpCookie cookie = request.getCookies().getFirst("token");
        if (!StringUtils.isEmpty(cookie)) {
            // 不为空,返回
            userToken = cookie.getValue();
        } else {
            // 为空,从请求头中获取token
            userToken = request.getHeaders().getFirst("token");
        }
        return userToken;
    }
}
