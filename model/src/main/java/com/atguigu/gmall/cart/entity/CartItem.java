package com.atguigu.gmall.cart.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车中的一个商品信息
 */
@Data
public class CartItem {
    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "skuid")
    private Long skuId;

    @ApiModelProperty(value = "放入购物车的价格")
    private BigDecimal cartPrice; // 100元

    @ApiModelProperty(value = "实时价格")
    private BigDecimal skuPrice; // 90元 (提示降价10元)

    @ApiModelProperty(value = "数量")
    private Integer skuNum;

    @ApiModelProperty(value = "图片文件")
    private String skuDefaultImg;

    @ApiModelProperty(value = "sku名称(冗余)")
    private String skuName;

    @ApiModelProperty(value = "isChecked")
    private Integer isChecked = 1;

    private Date createTime;

    private Date updateTime;


}
