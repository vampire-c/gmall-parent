package com.atguigu.gmall.order.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TestListener {

    // @RabbitListener(queues = MqConst.ORDER_CLOSE_QUEUE)
    public void testListener() {

    }


    // @RabbitListener(queues = "testQueue")
    public void test(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        byte[] body = message.getBody();
        try {
            System.out.println("收到消息" + new String(body));
            // 业务成功删除此消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
        }

    }
}
