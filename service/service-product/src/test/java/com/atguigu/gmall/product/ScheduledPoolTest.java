package com.atguigu.gmall.product;


import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledPoolTest {

    ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);

    @Test
    public void test() throws InterruptedException {
        pool.schedule(() -> {
            System.out.println("提交延迟任务");
        }, 4, TimeUnit.SECONDS);
        pool.scheduleAtFixedRate(() -> {
            System.out.println("提交固定频率执行的任务");
        }, 3, 2, TimeUnit.SECONDS);
        Thread.sleep(10000000);
    }
}
