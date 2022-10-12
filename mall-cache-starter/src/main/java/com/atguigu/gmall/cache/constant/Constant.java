package com.atguigu.gmall.cache.constant;

import java.util.concurrent.TimeUnit;

public class Constant {
    public static final String REDIS_LOCK_PREFIX = "lock-";

    public static final String TEMP_DATA = "x";


    // "x" 临时数据
    public static final long TEMP_DATA_TTL_X = 1L;
    public static final TimeUnit TEMP_DATA_TTL_UNIT_X = TimeUnit.HOURS;
}
