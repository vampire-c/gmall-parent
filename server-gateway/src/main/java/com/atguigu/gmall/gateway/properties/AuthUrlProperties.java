package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthUrlProperties {
    // 无需验证资源
    private List<String> noAuthUrl;
    // 需要验证资源
    private List<String> loginAuthUrl;
    // 登陆页
    private String loginPage;
    // 浏览器不可访问资源
    private List<String> denyUrl;


}
