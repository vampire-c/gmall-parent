package com.atguigu.gmall.cache.service;


import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.TimeUnit;

public interface CacheOpsService {


    /**
     * 查询缓存
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String key, Class<T> clazz);

    /**
     * 保存数据到缓存
     *
     * @param key
     * @param object
     */
    void saveCacheData(String key, Object object, Long ttl, TimeUnit timeUnit);


    <T> T getCacheData(String key, TypeReference<T> typeReference);
}
