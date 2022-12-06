package com.atguigu.gmall.util.service;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public interface RabbitService {

    public void retry(Channel channel, Long deliveryTag, String msgContent, int count) throws IOException;
}
