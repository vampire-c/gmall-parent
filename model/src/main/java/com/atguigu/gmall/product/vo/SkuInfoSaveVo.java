package com.atguigu.gmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfoSaveVo {

    private Long id;
    private Long spuId;
    private BigDecimal price;
    private String skuName;
    private BigDecimal weight;
    private String skuDesc;
    private Long category3Id;
    private String skuDefaultImg;
    private Long tmId;
    private List<SkuAttrValueList> skuAttrValueList;
    private List<SkuSaleAttrValueList> skuSaleAttrValueList;
    private List<SkuImageList> skuImageList;

    @Data
    public static class SkuAttrValueList {
        private Long attrId;
        private String valueId;
    }

    @Data
    public static class SkuSaleAttrValueList {
        private Long saleAttrValueId;
        private String saleAttrValueName;
        private Long baseSaleAttrId;
        private String saleAttrName;
    }

    @Data
    public static class SkuImageList {
        private Long spuImgId;
        private String imgName;
        private String imgUrl;
        private Integer isDefault;
    }
}
