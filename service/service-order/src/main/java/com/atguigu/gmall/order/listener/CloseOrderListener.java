package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.to.OrderMsgTo;
import com.atguigu.gmall.util.consts.MqConst;
import com.atguigu.gmall.util.service.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
public class CloseOrderListener {

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 消费关闭订单消息
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = MqConst.ORDER_CLOSE_QUEUE)
    public void closeOrder(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        OrderMsgTo orderMsgTo = null;
        // 获取消息详情
        String json = new String(message.getBody());
        try {
            orderMsgTo = Jsons.toObject(json, OrderMsgTo.class);
            log.info("监听到关闭订单: {}", orderMsgTo);

            // 模拟发生异常
            // int i = 1 / 0;

            /*
            当业务出现问题，导致消费失败，返回给MQ == 消息又抵达过来，成为一个无限死循环
            消息重复：
            1）、消费失败（业务失败），返回给mq服务器重新入队以后，重新派发过来
            2）、消费成功（业务成功），没来得及回复，炸了，重新派发过来
            现象：同一个关单消息被订单关单服务收到很多次
            解决：
             1、一定保证消费消息的业务是幂等的：关单是幂等性操作；
             2、判断这个消息是否重复消费的。[业务、redelivered、每个消息有唯一id]
             3、有限次尝试。如果有消息一直失败，保存到告警库，然后人工处理
             */

            // 关闭订单
            orderBizService.closeOrder(orderMsgTo.getOrderId(), orderMsgTo.getUserId());
            // 回复消息完成
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            // 消息重试
            rabbitService.retry(channel, deliveryTag, json, 5);

            /*
            String jsonMD5 = MD5.encrypt(json);
            // 给redis添加重试记录
            Long increment = redisTemplate.opsForValue().increment(RedisConst.MQ_RETRY + jsonMD5);
            if (increment <= 5) {
                log.info("消息消费失败: {}", orderMsgTo);
                channel.basicNack(deliveryTag, false, true);
            } else {
                log.error("消息消费重试超过最大次数:{}", orderMsgTo);
            }
            */
        }
    }
}
