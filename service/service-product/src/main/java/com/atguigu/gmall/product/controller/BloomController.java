package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.BloomService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "商品属性SKU管理接口")
@RequestMapping("/admin/product")
@RestController
public class BloomController {

    @Autowired
    private BloomService bloomService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 重置布隆
     *
     * @return
     */
    @GetMapping("/bloom/reset")
    public Result resetBloom() {
        bloomService.resetBloom(RedisConst.BLOOM_SKUID);
        return Result.ok(redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID).contains(999L));
    }
}
