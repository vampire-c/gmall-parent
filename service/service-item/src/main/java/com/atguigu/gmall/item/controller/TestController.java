package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // ReentrantLock lock = new ReentrantLock();

    @Value("${server.port}")
    String port;

    @GetMapping("/test")
    public Result test() {
        // System.out.println(port);
        // 加锁
        // lock.lock(); //本地锁
        // 分布式锁
        String uuid = UUID.randomUUID().toString();
        lcok(uuid); // 循环抢锁

        test0(); // 业务代码

        // 解锁
        // lock.unlock(); // 本地锁
        // 分布式锁
        unlock(uuid);
        return Result.ok();
    }

    /**
     * 使用脚本执行redis命令解锁
     *
     * @param uuid
     */
    private void unlock(String uuid) {
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList("lock"),
                uuid);
    }

    /**
     * 循环抢锁
     *
     * @param uuid
     */
    private void lcok(String uuid) {
        while (!redisTemplate.opsForValue().setIfAbsent("lock", uuid, 60, TimeUnit.SECONDS)) {
            // 循环抢锁
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void test0() {
        String test = redisTemplate.opsForValue().get("test");
        int i = Integer.parseInt(test);
        i++;
        redisTemplate.opsForValue().set("test", String.valueOf(i));
    }
/*

    @Qualifier("otherPool")
    @Autowired
    private ThreadPoolExecutor otherPool;

    @GetMapping("/close/pool")
    public Result closePool() {
        otherPool.shutdown();
        return Result.ok();
    }
*/

}
