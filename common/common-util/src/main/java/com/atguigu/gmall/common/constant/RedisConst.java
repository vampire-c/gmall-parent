package com.atguigu.gmall.common.constant;

import java.util.concurrent.TimeUnit;

public class RedisConst {
    // skuInfo缓存前缀
    public static final String SKU_DETAIL_CACHE_PREFIX = "sku:info:";

    public static final String CATEGORYS_CACHE = "categorys:tree";

    // 分布式锁前缀
    public static final String REDIS_LOCK_PREFIX = "lock-";

    public static final String TEMP_DATA = "x";

    // "x" 临时数据
    public static final long TEMP_DATA_TTL_X = 1L;
    public static final TimeUnit TEMP_DATA_TTL_UNIT_X = TimeUnit.HOURS;

    // skuId布隆过滤器
    public static final String BLOOM_SKUID = "skuId-bloom";

    // 时间
    public static final Long TEMP_DATA_TTL = 7L;
    public static final TimeUnit TEMP_DATA_TTL_UNIT = TimeUnit.DAYS;
}
