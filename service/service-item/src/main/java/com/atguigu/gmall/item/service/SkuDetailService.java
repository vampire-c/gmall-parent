package com.atguigu.gmall.item.service;

import com.atguigu.gmall.web.SkuDetailVo;

public interface SkuDetailService {

    /**
     * 查询商品信息, 并返回指定类型数据
     * @param skuId
     * @return
     */
    SkuDetailVo getSkuDetail(Long skuId);



}
