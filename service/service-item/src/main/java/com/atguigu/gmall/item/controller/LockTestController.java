package com.atguigu.gmall.item.controller;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.UUID;

@RestController
public class LockTestController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/lock")
    public String lock() throws InterruptedException {
        // 获取锁
        RLock lock = redissonClient.getLock("lock");
        // RLock fairLock = redissonClient.getFairLock("fairLock"); // 公平锁

        // 加锁

        // 阻塞式加锁
        lock.lock();
        // lock.lock(10, TimeUnit.SECONDS); // 可以加到期时间

        // 尝试一次加锁
        // waitTime 等待时间
        // leaseTime 释放时间
        // lock.tryLock();
        // lock.tryLock(10, 10, TimeUnit.SECONDS);

        System.out.println(Thread.currentThread() + "加锁");
        try {
            Thread.sleep(10000);
            lock.lock();
            System.out.println(Thread.currentThread() + "重入锁");

            // int i = 1 / 0;
            Thread.sleep(10000);
        } finally {
            // 解锁
            lock.unlock();
            System.out.println(Thread.currentThread() + "解锁");
            lock.unlock();
            System.out.println(Thread.currentThread() + "解锁");
        }
        return "ok";
    }

    String val = "77777777777777";

    @GetMapping("/readLock")
    public String readLock() throws InterruptedException {
        RReadWriteLock lock = redissonClient.getReadWriteLock("readLock");

        // 获取读锁
        RLock readLock = lock.readLock();

        try {
            readLock.lock();
            System.out.println(Thread.currentThread() + "readLock 加锁");
            Thread.sleep(5000);
            return val;
        } finally {
            readLock.unlock();
            System.out.println(Thread.currentThread() + "readLock 解锁");
        }
    }


    @GetMapping("/writeLock")
    public String writeLock() throws InterruptedException {
        RReadWriteLock lock = redissonClient.getReadWriteLock("readLock");

        RLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            System.out.println(Thread.currentThread() + "writeLock 加锁");
            val = UUID.randomUUID().toString();
            Thread.sleep(5000);
            return val;
        } finally {
            writeLock.unlock();
            System.out.println(Thread.currentThread() + "writeLock 解锁");
        }
    }


    @PostConstruct
    public void tccInit() {
        RSemaphore semaphore = redissonClient.getSemaphore("tcc");
        semaphore.trySetPermits(3);

    }


    @GetMapping("/tcc/status")
    public String tcc() {
        RSemaphore semaphore = redissonClient.getSemaphore("tcc");
        return "剩余" + semaphore.availablePermits();
    }

    @GetMapping("/tcc/park")
    public String tc() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("tcc");
        semaphore.acquire();
        return "停车成功";
    }

    @GetMapping("/tcc/go")
    public String go() {
        RSemaphore semaphore = redissonClient.getSemaphore("tcc");
        semaphore.release();
        return "停车结束";
    }


    @GetMapping("/req")
    public String req() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("tcc");
        boolean b = semaphore.tryAcquire();
        if (b) {
            Thread.sleep(10000);
            semaphore.release();
            return "请求完成";
        } else {
            System.out.println("111111111111");
            return "请求中......";
        }
    }


}
