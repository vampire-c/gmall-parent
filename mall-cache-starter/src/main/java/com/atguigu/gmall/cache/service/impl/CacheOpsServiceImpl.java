package com.atguigu.gmall.cache.service.impl;

import com.atguigu.gmall.cache.constant.Constant;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.atguigu.gmall.util.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查询缓存
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheData(String key, Class<T> clazz) {
        // redis中查询缓存
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            T t = Jsons.toObject(json, clazz);
            return t;
        }
    }

    /**
     * 保存数据到缓存
     *
     * @param key
     * @param object
     */
    @Override
    public void saveCacheData(String key, Object object, Long ttl, TimeUnit timeUnit) {
        // 如果数据为空 放入缓存"x", 不为空放入数据
        String json = StringUtils.isEmpty(object) ? Constant.TEMP_DATA : Jsons.toString(object);
        if (StringUtils.isEmpty(object)) {
            redisTemplate.opsForValue().set(key, json, Constant.TEMP_DATA_TTL_X, Constant.TEMP_DATA_TTL_UNIT_X);
        } else {
            redisTemplate.opsForValue().set(key, json, ttl, timeUnit);
        }


    }

    @Override
    public <T> T getCacheData(String key,  TypeReference<T> typeReference) {
        // redis中查询缓存
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            T t = Jsons.toObject(json, typeReference);
            return t;
        }
    }
}
