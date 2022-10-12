package com.atguigu.gmall.web;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * sku详情页面Vo
 */
@Data
public class SkuDetailVo {
    CategoryView categoryView;
    SkuInfo skuInfo;
    BigDecimal price;
    List<SpuSaleAttr> spuSaleAttrList;
    String valuesSkuJson;

    @Data
    public static class CategoryView {
        private Long category1Id;
        private String category1Name;
        private Long category2Id;
        private String category2Name;
        private Long category3Id;
        private String category3Name;
    }

}
