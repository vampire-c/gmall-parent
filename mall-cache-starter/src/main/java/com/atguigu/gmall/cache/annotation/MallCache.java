package com.atguigu.gmall.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MallCache {

    // 缓存key
    String cacheKey() default "";

    // 布隆key
    String bloomKey() default "";

    // 布隆值
    String bloomValue() default "";

    // 使用分布式锁
    boolean enableLock() default true;

    // 缓存存储时间
    long ttl() default 30L;

    // 缓存存储时间单位
    TimeUnit unit() default TimeUnit.MINUTES;
}
