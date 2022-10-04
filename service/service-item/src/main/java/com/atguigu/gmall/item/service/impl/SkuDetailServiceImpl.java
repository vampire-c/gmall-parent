package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {


    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient;
    //Could not autowire. No beans of 'SkuDetailFeignClient' type found.

    /**
     * 查询商品信息, 并返回指定类型数据
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {
        SkuDetailVo skuDetailVo = new SkuDetailVo();

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

        // 3.sku价格
        BigDecimal price = skuDetailFeignClient.getSkuInfoPrice(skuId).getData();
        skuDetailVo.setPrice(price);

        // 4.sku销售属性
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList = skuDetailFeignClient.getSpuSaleAttrAndValueAndMarkSkuZH(spuId, skuId).getData();
        skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);

        // 5.Json
        String json = skuDetailFeignClient.getSpuValuesSkuJson(spuId).getData();
        skuDetailVo.setValuesSkuJson(json);

        return skuDetailVo;
    }
}
