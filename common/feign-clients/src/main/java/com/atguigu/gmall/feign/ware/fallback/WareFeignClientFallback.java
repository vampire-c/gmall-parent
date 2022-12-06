package com.atguigu.gmall.feign.ware.fallback;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WareFeignClientFallback implements WareFeignClient {
    @Override
    public String hasStock(Long skuId, Integer num) {
        log.info("Feign远程 查询是否有库存异常...");
        return "1";
    }
}
