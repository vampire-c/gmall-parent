package com.atguigu.gmall.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {
        String str = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("a", str);
        String s = redisTemplate.opsForValue().get("a");
        System.out.println(s);
    }

}
