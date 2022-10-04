package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 根据spuId查询销售属性的名和值
     *
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrAndValue(Long spuId);

    /**
     * 查询指定sku对应的spu定义的所有属性名和值,并且标记当前sku属性
     *
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrAndValueAndMarkSkuZH(Long spuId, Long skuId);

    /**
     * 根据spuId查询该spu下所有sku涉及到的所有销售属性值组合
     *
     * @param spuId
     * @return
     */
    String getSpuValuesSkuJson(Long spuId);
}
