package com.atguigu.gmall.redisson.annotation;

import com.atguigu.gmall.redisson.RedissonConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(RedissonConfiguration.class)
public @interface EnableRedisson {
}
