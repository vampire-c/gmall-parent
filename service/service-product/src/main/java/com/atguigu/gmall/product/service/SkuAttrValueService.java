package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 查询某商品的所有平台属性名和值
     *
     * @param skuId
     * @return
     */
    List<SearchAttr> getSkuAttrAndValue(Long skuId);
}
