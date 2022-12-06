package com.atguigu.gmall.util.config;

import com.atguigu.gmall.util.service.RabbitService;
import com.atguigu.gmall.util.service.impl.RabbitServiceImpl;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@EnableRabbit
@Configuration
public class MyRabbitConfiguration {

    @Bean
    public RabbitService rabbitService() {
        return new RabbitServiceImpl();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            CachingConnectionFactory rabbitConnectionFactory,
            RabbitTemplateConfigurer configurer
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);
        configurer.configure(rabbitTemplate, rabbitConnectionFactory);

        // 返回回调
        // 消息是否抵达队列

        rabbitTemplate.setReturnCallback((
                message,
                replyCode,
                replyText,
                exchange,
                routingKey) -> {
            System.out.println("ReturnCallback...状态码:" + replyCode + "...状态文本:" + replyText);
        });

        // 确认回调
        // 正常消息：  服务器收到消息，并且存储到队列中。回复ack = true
        // 错误消息：
        // 1）交换机错误：ConfirmCallback： ack：false
        // 2）路由键错误： ReturnCallback 状态码:312(感知到错误), ConfirmCallback ack:true(交换机收到了)
        // 3）队列抵达错误：ReturnCallback = 312，ConfirmCallback = true
        rabbitTemplate.setConfirmCallback((
                @Nullable CorrelationData correlationData,
                boolean ack, // 交换机是否收到消息
                @Nullable String cause
        ) -> {
            System.out.println("ConfirmCallback...ack(是否收到):" + ack + "...原因:" + cause);
        });

        return rabbitTemplate;
    }

}
