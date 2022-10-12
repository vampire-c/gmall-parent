package com.atguigu.gmall.product.cron;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.service.BloomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BloomTask {


    @Autowired
    private BloomService bloomService;

    @Scheduled(cron = "0 0 3 */7 * ?")
    public void resetTaskBloom() {
        log.info("布隆定时重建...");
        bloomService.resetBloom(RedisConst.BLOOM_SKUID);
    }
}
