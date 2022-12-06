package com.atguigu.gmall.order.config;

import com.atguigu.gmall.util.consts.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class OrderMyConfiguration {

    /**
     * 订单事件交换机
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(MqConst.ORDER_EVENT_EXCHANGE, true, false);
    }

    /**
     * 订单延时队列
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        HashMap<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MqConst.ORDER_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key", MqConst.RK_ORDER_DELAY);
        args.put("x-message-ttl", MqConst.ORDER_DELAY_TTL);
        // args.put("x-message-ttl", 60000);
        return new Queue(MqConst.ORDER_DELAY_QUEUE, true, false, false, args);
    }

    /**
     * 订单交换机和延迟队列 绑定关系
     *
     * @return
     */
    @Bean
    public Binding orderDelayBinding() {
        /*
         * String destination                           目的地
         * Binding.DestinationType destinationType      目的地类型(队列,交换机)
         * String exchange                              交换机
         * String routingKey                            路由键
         * @Nullable Map<String, Object> arguments      参数项
         */
        return new Binding(
                MqConst.ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_CREATED,
                null);
    }


    /**
     * 关闭订单队列
     *
     * @return
     */
    @Bean
    public Queue orderCloseQueue() {
        return new Queue(MqConst.ORDER_CLOSE_QUEUE, true, false, false);
    }


    /**
     * 订单交换机和关闭订单队列 绑定关系
     *
     * @return
     */
    @Bean
    public Binding orderCloseBinding() {
        /*
         * String destination                           目的地
         * Binding.DestinationType destinationType      目的地类型(队列,交换机)
         * String exchange                              交换机
         * String routingKey                            路由键
         * @Nullable Map<String, Object> arguments      参数项
         */
        return new Binding(
                MqConst.ORDER_CLOSE_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_DELAY,
                null);
    }

    /**
     * 支付成功订单队列
     *
     * @return
     */
    @Bean
    public Queue orderPayedQueue() {
        return new Queue(MqConst.ORDER_PAYED_QUEUE, true, false, false);
    }

    /**
     * 订单交换机和支付成功订单队列 绑定关系
     * @return
     */
    @Bean
    public Binding orderPayedBinding() {
        /*
         * String destination                           目的地
         * Binding.DestinationType destinationType      目的地类型(队列,交换机)
         * String exchange                              交换机
         * String routingKey                            路由键
         * @Nullable Map<String, Object> arguments      参数项
         */
        return new Binding(
                MqConst.ORDER_PAYED_QUEUE,
                Binding.DestinationType.QUEUE,
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_PAYED,
                null);
    }



}
