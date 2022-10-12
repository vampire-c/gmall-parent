package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.product.vo.SkuInfoSaveVo;
import com.atguigu.gmall.product.vo.SkuInfoUpdateVo;
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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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


}




