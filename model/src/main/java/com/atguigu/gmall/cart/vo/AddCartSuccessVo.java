package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddCartSuccessVo {
    private SkuInfo skuInfo;
    private Integer skuNum;
}
