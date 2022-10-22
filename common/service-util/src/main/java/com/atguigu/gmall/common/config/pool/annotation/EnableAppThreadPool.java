package com.atguigu.gmall.common.config.pool.annotation;

import com.atguigu.gmall.common.config.pool.AppThreadPoolConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义线程池
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({
        AppThreadPoolConfiguration.class
})
public @interface EnableAppThreadPool {
}