package com.atguigu.gmall.product.service.impl;

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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuInfoPrice(Long skuId) {
        return skuInfoMapper.getSkuInfoPrice(skuId);
    }


}




