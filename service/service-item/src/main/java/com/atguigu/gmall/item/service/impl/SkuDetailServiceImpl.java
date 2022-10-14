package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.cache.annotation.MallCache;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {


    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient;
    //Could not autowire. No beans of 'SkuDetailFeignClient' type found.

    // Set<Long> skuId = new ConcurrentHashSet<>();

    // 使用redisson.getBloomFilter
    // BloomFilter<Long> bloomFilter = null; // 本地布隆
    // RBloomFilter<Object> skuIdBloom = null; // 分布式布隆

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 本地锁, 用于限制大量请求同时访问数据库
    // ReentrantLock lock = new ReentrantLock();

    @Autowired
    private CacheOpsService cacheOpsService;


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    //初始化布隆过滤器
    /*
    @PostConstruct
    public void init() {
        log.info("初始化布隆过滤器...");
        // 创建布隆过滤器
        // this.bloomFilter = BloomFilter.create(Funnels.longFunnel(), 5000000, 0.0000001); // 本地布隆
        skuIdBloom = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID); // 分布式布隆过滤器
        if (!skuIdBloom.isExists()) {
            skuIdBloom.tryInit(1000000, 0.000001);
            // 数据库中所有商品的id
            List<Long> skuIds = skuDetailFeignClient.getAllSkuIds().getData();
            // 将所有id添加到布隆过滤器
            // skuIds.forEach(item -> this.bloomFilter.put(item)); // 本地布隆
            skuIds.forEach(item -> skuIdBloom.add(item)); // 分布式布隆

            // log.info("初始化布隆过滤器完成...49:{}, 50{}", this.bloomFilter.mightContain(49L), this.bloomFilter.mightContain(50L));
            log.info("初始化分布式布隆过滤器完成...49:{}, 50{}", skuIdBloom.contains(49L), skuIdBloom.contains(50L));
        }
    }
     */


    /**
     * 查询商品信息, 并返回指定类型数据
     *
     * @param skuId
     * @return
     */
    @MallCache(cacheKey = RedisConst.SKU_DETAIL_CACHE_PREFIX + "#{#args[0]}",
            bloomKey = RedisConst.BLOOM_SKUID,
            bloomValue = "#{#args[0]}",
            ttl = 7L,
            unit = TimeUnit.DAYS)
    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {
        SkuDetailVo skuDetailVo = getSkuDetailVo(skuId);
        return skuDetailVo;
    }


    /**
     * feign远程调用从数据库获取数据 (数据回源)
     *
     * @param skuId
     * @return
     */
    private SkuDetailVo getSkuDetailVo(Long skuId) {
        SkuDetailVo skuDetailVo = new SkuDetailVo();

        // 异步编排

        // 1.1 查sku_info信息
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("正在回源skuInfo信息...");
            SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
            skuDetailVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        // 1.2 查询图片数据
        CompletableFuture<Void> skuImageListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SkuImage> skuImageList = skuDetailFeignClient.getSkuImages(skuId).getData();
            // 1.2 赋值图片数据
            skuInfo.setSkuImageList(skuImageList);
        }, threadPoolExecutor);

        // 2.sku的三层分类信息
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            SkuDetailVo.CategoryView categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id()).getData();
            skuDetailVo.setCategoryView(categoryView);
        }, threadPoolExecutor);

        // 3.sku价格
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            log.info("正在回源sku价格...");
            BigDecimal price = skuDetailFeignClient.getSkuInfoPrice(skuId).getData();
            skuDetailVo.setPrice(price);
        }, threadPoolExecutor);

        // 4.sku销售属性
        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrList = skuDetailFeignClient.getSpuSaleAttrAndValueAndMarkSkuZH(skuInfo.getSpuId(), skuId).getData();
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);
        }, threadPoolExecutor);

        // 5.Json
        CompletableFuture<Void> jsonStrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            String jsonStr = skuDetailFeignClient.getSpuValuesSkuJson(skuInfo.getSpuId()).getData();
            skuDetailVo.setValuesSkuJson(jsonStr);
        });

        // 这些任务都结束后返回skuDetailVo
        CompletableFuture.allOf(
                skuImageListCompletableFuture,      // 查询图片数据
                categoryViewCompletableFuture,      // sku的三层分类信息
                priceCompletableFuture,             // sku价格
                spuSaleAttrListCompletableFuture,   // sku销售属性
                jsonStrCompletableFuture            // Json
        ).join();

        log.info("正在回源...");
        return skuDetailVo;
    }

    // 自定义线程池+闭锁回源
    /*
    private SkuDetailVo getSkuDetailVo(Long skuId) {
        SkuDetailVo skuDetailVo = new SkuDetailVo();
        CountDownLatch countDownLatch = new CountDownLatch(2);

        threadPoolExecutor.execute(() -> {
            log.info("正在回源skuInfo信息...");

            // 1.1 查sku_info信息
            SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
            // 1.2 查询图片数据
            List<SkuImage> skuImageList = skuDetailFeignClient.getSkuImages(skuId).getData();
            // 1.2 赋值图片数据
            skuInfo.setSkuImageList(skuImageList);
            // 1.1 赋值sku_info
            skuDetailVo.setSkuInfo(skuInfo);

            // 2.sku的三层分类信息
            SkuDetailVo.CategoryView categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id()).getData();
            skuDetailVo.setCategoryView(categoryView);

            // 4.sku销售属性
            Long spuId = skuInfo.getSpuId();
            List<SpuSaleAttr> spuSaleAttrList = skuDetailFeignClient.getSpuSaleAttrAndValueAndMarkSkuZH(spuId, skuId).getData();
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);

            // 5.Json
            String jsonStr = skuDetailFeignClient.getSpuValuesSkuJson(spuId).getData();
            skuDetailVo.setValuesSkuJson(jsonStr);

            countDownLatch.countDown();
        });

        threadPoolExecutor.execute(() -> {
            log.info("正在回源sku价格...");
            // 3.sku价格
            BigDecimal price = skuDetailFeignClient.getSkuInfoPrice(skuId).getData();
            skuDetailVo.setPrice(price);

            countDownLatch.countDown();
        });

        try {
            log.info("正在回源...");
            countDownLatch.await(); // 等待任务执行完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return skuDetailVo;
    }
    */

    // 查询商品详情(分布式锁,布隆过滤器)
    public SkuDetailVo getSkuDetailWithRedissonLockAndBloomFilter(Long skuId) {
        String key = RedisConst.SKU_DETAIL_CACHE_PREFIX + skuId;
        // 1 先查缓存
        SkuDetailVo skuDetailVo = cacheOpsService.getCacheData(key, SkuDetailVo.class);

        // 2 缓存存在则直接返回
        if (!StringUtils.isEmpty(skuDetailVo)) {
            return skuDetailVo;
        }

        // 3  缓存中没有,问布隆过滤器数据库中是否存在
        boolean contain = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID).contains(skuId);
        if (!contain) {
            return null;
        }

        // 4 布隆说数据库存在, 回源
        // 5 使用分布式锁, 防止缓存击穿
        RLock lock = redissonClient.getLock(RedisConst.REDIS_LOCK_PREFIX + skuId);

        // 6 尝试获取一次锁, 加锁
        boolean tryLock = lock.tryLock();

        if (tryLock) {
            // 7 拿到锁回源
            try {
                // 双检查机制, 再次检查缓存
                SkuDetailVo skuDetailVo1 = cacheOpsService.getCacheData(key, SkuDetailVo.class);

                // 8 再次检查缓存为空
                if (StringUtils.isEmpty(skuDetailVo1)) {
                    // 9 回源
                    skuDetailVo1 = getSkuDetailVo(skuId);
                    log.info("商品详情回源... {}", skuId);

                    // 10 数据放入缓存
                    cacheOpsService.saveCacheData(key, skuDetailVo1, RedisConst.TEMP_DATA_TTL, RedisConst.TEMP_DATA_TTL_UNIT);
                }
                return skuDetailVo1;
            } finally {
                // 11 解锁
                lock.unlock();
            }
        } else {
            // 7 没拿到锁
            try {
                // 等500ms后从缓存中获取数据
                Thread.sleep(500);
                return cacheOpsService.getCacheData(key, SkuDetailVo.class);
            } catch (InterruptedException e) {

            }
        }
        return skuDetailVo;
    }

    public SkuDetailVo getSkuDetailWithRedisLock(Long skuId) {
        String uuid = UUID.randomUUID().toString();

        // 加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (lock) {

            // 自动续期
            /*
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
                }
            });
            */

            // 回源
            SkuDetailVo skuDetailVo = getSkuDetailVo(skuId);
            // 放入缓存
            redisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toString(skuDetailVo));

            // 判断锁是否是自己加的 (不具有原子性)
            /*
            String lockValue = redisTemplate.opsForValue().get("lock");
            if (lockValue.equals(uuid)) {
                // 解锁
                redisTemplate.delete("lock");
            }
             */

            // lua脚本执行redis,命令
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                    Arrays.asList("lock"),
                    uuid);

        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String json = redisTemplate.opsForValue().get("sku:info:" + skuId);
            SkuDetailVo skuDetailVo = Jsons.toObject(json, SkuDetailVo.class);
            return skuDetailVo;
        }

        return null;

    }

    //本地锁
    /*
    //查询商品信息, 并返回指定类型数据
    public SkuDetailVo getSkuDetail(Long skuId) {
        // 布隆过滤器判断是否存在
        if (!bloomFilter.mightContain(skuId)) {
            log.info("商品详情-布隆过滤器拦截-数据库中没有该商品--{}", skuId);
            return null;
        }

        // 查询缓存
        String json = redisTemplate.opsForValue().get("sku:info:" + skuId);

        // 缓存中存在, 封装为返回类型, 返回
        if (!StringUtils.isEmpty(json)) {
            SkuDetailVo skuDetailVo = Jsons.toObject(json, SkuDetailVo.class);
            return skuDetailVo;
        }

        //  某一请求抢到锁
        if (lock.tryLock()) {
            // 数据回源
            SkuDetailVo skuDetailVo = getSkuDetailVo(skuId);
            log.info("数据回源完成: {}", skuDetailVo);
            // 数据库中没有查到数据, 给缓存中放入"x"
            String data = StringUtils.isEmpty(skuDetailVo) ? "x" : Jsons.toString(skuDetailVo);
            // 将返回的数据放入缓存, 下次查询直接查缓存
            redisTemplate.opsForValue().set("sku:info:" + skuId, data);
            log.info("数据放入缓存");
            return skuDetailVo;
        } else {
            // 没有抢到锁的请求再查缓存
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            json = redisTemplate.opsForValue().get("sku:info:" + skuId);
            SkuDetailVo skuDetailVo = Jsons.toObject(json, SkuDetailVo.class);
            return skuDetailVo;
        }
    }
    */
}
