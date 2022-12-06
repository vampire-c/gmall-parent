package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.util.consts.MqConst;
import com.atguigu.gmall.util.service.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class PayOrderListener {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private OrderBizService orderBizService;

    /**
     * 消费支付成功消息
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = MqConst.ORDER_PAYED_QUEUE)
    public void payedOrder(Message message, Channel channel) throws IOException {
        log.info("监听到支付成功消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String json = new String(message.getBody());
        try {
            // 修改订单为已支付
            orderBizService.updateOrderStatusPayed(json);
            // 回复消息完成
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 消息重试
            rabbitService.retry(channel, deliveryTag, json, 5);
        }
    }

}
