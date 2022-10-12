package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.BloomService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class BloomServiceImpl implements BloomService {


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 重建布隆
     *
     * @param bloomSkuId
     */
    @Override
    public void resetBloom(String bloomSkuId) {
        log.info("重置布隆...");
        // 创建一个新的布隆
        RBloomFilter<Object> bloomFilterNew = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID + "-new");
        if (!bloomFilterNew.isExists()) {
            // 如果布隆不存在
            // 初始化布隆
            bloomFilterNew.tryInit(1000000, 0.000001);
            // 给新布隆添加ids
            skuInfoMapper.getAllSkuIds().forEach(item -> bloomFilterNew.add(item));
            log.info("初始化新布隆...");

            bloomFilterNew.add(999L);
        }
        // // 删除旧布隆
        // redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID).delete();
        // // 新布隆改名为旧布隆
        // bloomFilterNew.rename(RedisConst.BLOOM_SKUID);

        // 删除旧布隆,新布隆改名 使用脚本执行
        /*
        redis.call("del",KEYS[1]);
        redis.call("del","{"..KEYS[1].."}:config");
        redis.call("rename",KEYS[2],KEYS[1]);
        redis.call("rename","{"..KEYS[2].."}:config","{"..KEYS[1].."}:config");
        */
        String script = "redis.call(\"del\",KEYS[1]);\n" +
                "redis.call(\"del\",\"{\"..KEYS[1]..\"}:config\");\n" +
                "redis.call(\"rename\",KEYS[2],KEYS[1]);\n" +
                "redis.call(\"rename\",\"{\"..KEYS[2]..\"}:config\",\"{\"..KEYS[1]..\"}:config\");";
        log.info("删除旧布隆,新布隆改名");
        redisTemplate.execute(new DefaultRedisScript<>(script, Object.class),
                Arrays.asList(RedisConst.BLOOM_SKUID, RedisConst.BLOOM_SKUID + "-new"));
        log.info("布隆重置完成");

    }
}
