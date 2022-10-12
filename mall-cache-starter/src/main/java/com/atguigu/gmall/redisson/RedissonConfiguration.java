package com.atguigu.gmall.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword());
        // 看门狗时间
        // config.setLockWatchdogTimeout(30L);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    // 布隆
    /*
    @Bean
    public RBloomFilter<Object> skuIdBloom() {
        RedissonClient redissonClient = redissonClient();
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID);
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(1000000, 0.000001);
        }
        return bloomFilter;
    }
     */


}
