package com.atguigu.gmall.common.config.pool;

import com.atguigu.gmall.common.config.pool.properties.AppThreadPoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.*;


@EnableConfigurationProperties(AppThreadPoolProperties.class)
@Configuration
public class AppThreadPoolConfiguration {

    /*
    int corePoolSize, 核心线程大小
    int maximumPoolSize, 最大线程数
    long keepAliveTime, 空闲时间
    TimeUnit unit, 单位时间
    BlockingQueue<Runnable> workQueue, 排队队列
    ThreadFactory threadFactory, 线程工厂
    RejectedExecutionHandler handler 拒绝策略
     */
    @Primary // 主要
    @Bean
    public ThreadPoolExecutor corePool(AppThreadPoolProperties appThreadPoolProperties,
                                       @Value("${spring.application.name}") String applicationName) {
        return new ThreadPoolExecutor(appThreadPoolProperties.getCorePoolSize(),
                appThreadPoolProperties.getMaximumPoolSize(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(appThreadPoolProperties.getWorkQueueSize()),
                new ThreadFactory() {
                    int i = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        /*
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                // 跑任务前处理
                                r.run(); //跑任务
                                // 跑任务后处理
                            }
                        };
                        */

                        Thread thread = new Thread(() -> {
                            // 跑任务前处理
                            r.run(); //跑任务
                            // 跑任务后处理
                        });
                        thread.setName(applicationName + "-pool-" + (i++));
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 其他线程池
    /*
    @Bean
    public ThreadPoolExecutor otherPool(AppThreadPoolProperties appThreadPoolProperties,
                                        @Value("${spring.application.name}") String applicationName) {
        return new ThreadPoolExecutor(appThreadPoolProperties.getCorePoolSize(),
                appThreadPoolProperties.getMaximumPoolSize(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(appThreadPoolProperties.getWorkQueueSize()),
                new ThreadFactory() {
                    int i = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(() -> {
                            // 跑任务前处理
                            r.run(); //跑任务
                            // 跑任务后处理
                        });
                        thread.setName(applicationName + "-pool-" + (i++));
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
    */

}
