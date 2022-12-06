package com.atguigu.gmall.order.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class DetailVo {

    // 商品id
    @NotBlank(message = "商品id不能为空")
    private Long skuId;

    // 商品名
    @NotBlank(message = "商品名不能为空")
    private String skuName;

    // 商品数量
    @NotBlank(message = "商品数量不能为空")
    private Integer skuNum;

    // 商品价格
    @NotBlank(message = "订单价格不能为空")
    private BigDecimal orderPrice;

    // 默认图片
    @NotBlank(message = "商品默认图片不能为空")
    private String imgUrl;

    // 是否有库存
    private String hasStock = "1";


}
