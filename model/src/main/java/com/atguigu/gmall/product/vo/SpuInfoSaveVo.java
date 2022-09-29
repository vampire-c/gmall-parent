package com.atguigu.gmall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuInfoSaveVo {
    private Long id;
    private String spuName;
    private String description;
    private Long category3Id;
    private List<SpuImage2> spuImageList;
    private List<SpuSaleAttrList> spuSaleAttrList;
    private Long tmId;

    @Data
    public static class SpuImage2 {
        private String imgName;
        private String imgUrl;
    }

    @Data
    public static class SpuSaleAttrList {
        private Long baseSaleAttrId;
        private String saleAttrName;
        private List<SpuSaleAttrValueList> spuSaleAttrValueList;
    }

    @Data
    public static class SpuSaleAttrValueList {
        private Long baseSaleAttrId;
        private String saleAttrValueName;
    }
}

