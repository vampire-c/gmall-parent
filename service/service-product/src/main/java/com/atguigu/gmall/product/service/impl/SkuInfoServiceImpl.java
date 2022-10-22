package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.product.vo.SkuInfoSaveVo;
import com.atguigu.gmall.product.vo.SkuInfoUpdateVo;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Anonymous
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Slf4j
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private CategoryBizService categoryBizService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private SearchFeignClient searchFeignClient;

    /**
     * 初始化布隆
     */
    @PostConstruct
    public void initSkuIdBloom() {
        // 创建布隆过滤器
        RBloomFilter<Object> skuIdBloom = redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID);
        if (!skuIdBloom.isExists()) {
            log.info("初始化布隆过滤器...");
            // 如果不存在则初始化
            skuIdBloom.tryInit(1000000, 0.000001);
            // 数据库中所有商品的id
            List<Long> skuIds = skuInfoMapper.getAllSkuIds();
            skuIds.forEach(item -> skuIdBloom.add(item));
        }
        log.info("初始化分布式布隆过滤器完成...49:{}, 50{}", skuIdBloom.contains(49L), skuIdBloom.contains(50L));
    }

    /**
     * 分页查询SKU列表
     *
     * @param pageNum
     * @param limitNum
     * @return
     */
    @Override
    public Page<SkuInfo> getSpuInfoPage(Long pageNum, Long limitNum) {
        Page<SkuInfo> page = new Page<>(pageNum, limitNum);
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        return skuInfoMapper.selectPage(page, queryWrapper);
    }

    /**
     * 添加保存sku
     *
     * @param skuInfoSaveVo
     */
    @Override
    public void skuInfoService(SkuInfoSaveVo skuInfoSaveVo) {
        // 保存SkuInfo相关数据
        SkuInfo skuInfo = new SkuInfo();
        // 将vo中的数据对拷给和数据表匹配的实例
        BeanUtils.copyProperties(skuInfoSaveVo, skuInfo);
        skuInfoMapper.insert(skuInfo);

        Long skuId = skuInfo.getId();

        // 保存skuImageList相关
        List<SkuImage> skuImageList =
                skuInfoSaveVo.getSkuImageList() // 获取skuImageList 图片集合
                        .stream().map(item -> {
                            SkuImage skuImage = new SkuImage();
                            // 将vo中的数据对拷给和数据表匹配的实例
                            BeanUtils.copyProperties(item, skuImage);
                            // vo中没有的数据回填, skuId
                            skuImage.setSkuId(skuId);
                            return skuImage;
                        }).collect(Collectors.toList());
        skuImageService.saveBatch(skuImageList); //批量存储skuImage 图片数据

        // 保存skuAttrValueList相关
        List<SkuAttrValue> skuAttrValueList = skuInfoSaveVo.getSkuAttrValueList() // 获取skuAttrValueList集合
                .stream().map(item -> {
                    SkuAttrValue skuAttrValue = new SkuAttrValue();
                    // 将vo中的数据对拷给和数据表匹配的实例
                    BeanUtils.copyProperties(item, skuAttrValue);
                    // vo中没有的数据回填, skuId
                    skuAttrValue.setSkuId(skuId);
                    return skuAttrValue;
                }).collect(Collectors.toList());
        skuAttrValueService.saveBatch(skuAttrValueList); // 批量存储skuAttrValue

        // 保存skuSaleAttrValueList相关
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfoSaveVo.getSkuSaleAttrValueList() // 获取SkuSaleAttrValueList集合
                .stream().map(item -> {
                    SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
                    // 将vo中的数据对拷给和数据表匹配的实例
                    BeanUtils.copyProperties(item, skuSaleAttrValue);
                    // vo中没有的数据回填, spuId, skuId
                    skuSaleAttrValue.setSkuId(skuId);
                    skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList); // 批量存储SkuSaleAttrValue

        // 添加skuId到布隆
        redissonClient.getBloomFilter(RedisConst.BLOOM_SKUID).add(skuId);
        // 添加价格到redis缓存
        redisTemplate.opsForValue().set(RedisConst.SKU_PRICE + skuId, skuInfo.getPrice().toString());
        log.info("已保存, {}", skuId);
    }


    /**
     * 修改上下架状态
     *
     * @param skuId
     * @param status
     */
    @Override
    public void changeOnSale(Long skuId, int status) {
        skuInfoMapper.updateSaleStatus(skuId, status);
    }

    /**
     * 查询SkuInfo的价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuInfoPrice(Long skuId) {
        return skuInfoMapper.getSkuInfoPrice(skuId);
    }

    /**
     * 获取所有skuId
     *
     * @return
     */
    @Override
    public List<Long> getAllSkuIds() {
        return skuInfoMapper.getAllSkuIds();
    }


    // 伪 修改SkuInfo
    @Override
    public void updateSkuInfo(SkuInfoUpdateVo skuInfoUpdateVo) {
        // 数据库修改数据

        // 立即删除
        redisTemplate.delete(RedisConst.SKU_DETAIL_CACHE_PREFIX + skuInfoUpdateVo.getId());

        // 延迟再次删除, 不能设置队列大小, 有OOM分险
        pool.schedule(() -> {
            redisTemplate.delete(RedisConst.SKU_DETAIL_CACHE_PREFIX + skuInfoUpdateVo.getId());
        }, 10, TimeUnit.SECONDS);
    }

    /**
     * 上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        // 修改状态
        this.changeOnSale(skuId, 1);
        // 把商品sku封装到goods中
        Goods goods = prepareGoods(skuId);
        // 商品上架,数据保存到es
        searchFeignClient.onSaleGoods(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        // 修改状态
        this.changeOnSale(skuId, 0);
        // 删除es中的数据
        searchFeignClient.cancelSaleGoods(skuId);
    }

    /**
     * 生成某个商品在es中的储存的数据模型
     *
     * @param skuId
     * @return
     */
    private Goods prepareGoods(Long skuId) {
        Goods goods = new Goods();
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        CompletableFuture<Void> skuInfoCompletableFuture = CompletableFuture.runAsync(() -> {
            // 商品基本信息
            goods.setId(skuId);
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(skuInfo.getPrice());
            goods.setCreateTime(new Date());
        }, threadPoolExecutor);

        CompletableFuture<Void> baseTrademarkCompletableFuture = CompletableFuture.runAsync(() -> {
            // 品牌基本信息
            BaseTrademark baseTrademark = baseTrademarkMapper.selectById(skuInfo.getTmId());
            goods.setTmId(baseTrademark.getId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        }, threadPoolExecutor);


        CompletableFuture<Void> categoryViewEntityCompletableFuture = CompletableFuture.runAsync(() -> {
            // 商品分类信息
            CategoryViewEntity categoryViewEntity = categoryBizService.getCategoryView(skuInfo.getCategory3Id());
            goods.setCategory1Id(categoryViewEntity.getC1id());
            goods.setCategory1Name(categoryViewEntity.getC1name());
            goods.setCategory2Id(categoryViewEntity.getC2id());
            goods.setCategory2Name(categoryViewEntity.getC2name());
            goods.setCategory3Id(categoryViewEntity.getC3id());
            goods.setCategory3Name(categoryViewEntity.getC3name());
        }, threadPoolExecutor);

        // 商品热度(商品每次点击增加es中的热度分)
        goods.setHotScore(0L);

        CompletableFuture<Void> skuAttrAndValueCompletableFuture = CompletableFuture.runAsync(() -> {
            // 查询某商品的所有平台属性名和值
            List<SearchAttr> searchAttrList = skuAttrValueService.getSkuAttrAndValue(skuId);
            goods.setAttrs(searchAttrList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                baseTrademarkCompletableFuture,
                categoryViewEntityCompletableFuture,
                skuAttrAndValueCompletableFuture
        ).join();

        // 返回
        return goods;
    }


}




