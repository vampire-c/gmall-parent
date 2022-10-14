package com.atguigu.gmall.common.config.pool.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.threadpool")
public class AppThreadPoolProperties {
    int corePoolSize = 4;
    int maximumPoolSize = 8;
    long keepAliveTime = 1000*60*5L; //以毫秒为单位
    int workQueueSize = 1000; //压测来决定。 依据内存：调整为峰值的2~5倍；
    //队列防止oom。调大提升 吞吐量 = QPS
}