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
     * 查询销售属性的名和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);
}
