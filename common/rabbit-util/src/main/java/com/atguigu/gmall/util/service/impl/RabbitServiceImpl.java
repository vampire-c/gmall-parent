package com.atguigu.gmall.util.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.util.service.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

@Slf4j
public class RabbitServiceImpl implements RabbitService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void retry(Channel channel, Long deliveryTag, String msgContent, int count) throws IOException {
        String md5 = MD5.encrypt(msgContent);
        // 给redis添加重试记录
        Long increment = redisTemplate.opsForValue().increment(RedisConst.MQ_RETRY + md5);
        if (increment <= count) {
            log.info("消息消费失败: {} , 正在尝试第 {} 次", msgContent, increment);
            channel.basicNack(deliveryTag, false, true);
        } else {
            log.error("消息消费重试超过最大次数:{}", msgContent);
            redisTemplate.delete(RedisConst.MQ_RETRY + md5);
        }
    }
}
