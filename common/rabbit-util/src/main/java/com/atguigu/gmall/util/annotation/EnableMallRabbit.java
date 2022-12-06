package com.atguigu.gmall.util.annotation;

import com.atguigu.gmall.util.config.MyRabbitConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(MyRabbitConfiguration.class)
public @interface EnableMallRabbit {
}
