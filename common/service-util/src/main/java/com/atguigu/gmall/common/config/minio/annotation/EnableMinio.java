package com.atguigu.gmall.common.config.minio.annotation;


import com.atguigu.gmall.common.config.minio.config.MinioAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 上传服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(MinioAutoConfiguration.class)
public @interface EnableMinio {
}
