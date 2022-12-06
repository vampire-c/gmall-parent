package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.util.consts.MqConst;
import com.atguigu.gmall.util.service.RabbitService;
import com.atguigu.gmall.ware.WareStockResultMsg;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderStockResultListener {

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(
                            value = MqConst.QUEUE_WARE_ORDER,
                            declare = "true",
                            exclusive = "false",
                            autoDelete = "false"),
                    exchange = @Exchange(value = MqConst.WARE_ORDER_EXCHANGE),
                    key = MqConst.RK_WARE_ORDER
            )
    })
    public void orderStockListener(Message message, Channel channel) throws IOException {
        log.info("监听到订单扣减成功消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String json = new String(message.getBody());
        try {
            // 修改订单状态
            WareStockResultMsg wareStockResultMsg = Jsons.toObject(json, WareStockResultMsg.class);
            // 感知库存扣减, 修改订单状态
            orderBizService.updateOrderStatusByStockResult(wareStockResultMsg);

            // 回复消息完成
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 消息重试
            rabbitService.retry(channel, deliveryTag, json, 5);
        }
    }


}
