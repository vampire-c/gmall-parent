package com.atguigu.gmall.product.service;

public interface BloomService {

    /**
     * 重建布隆
     * @param bloomSkuId
     */
    void resetBloom(String bloomSkuId);
}
