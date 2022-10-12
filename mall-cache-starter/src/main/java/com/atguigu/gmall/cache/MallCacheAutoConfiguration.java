package com.atguigu.gmall.cache;

import com.atguigu.gmall.cache.aspect.MallCacheAspect;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.atguigu.gmall.cache.service.impl.CacheOpsServiceImpl;
import com.atguigu.gmall.redisson.annotation.EnableRedisson;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRedisson
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class MallCacheAutoConfiguration {

    @Bean
    public MallCacheAspect mallCacheAspect() {
        return new MallCacheAspect();
    }

    @Bean
    public CacheOpsService cacheOpsService() {
        return new CacheOpsServiceImpl();
    }
}
