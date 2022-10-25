package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.user.entity.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {


    // 商品列表
    private List<DetailVo> detailArrayList;

    // 商品总数
    private Integer totalNum;

    // 商品总金额
    private BigDecimal totalAmount;

    // 收货地址列表
    private List<UserAddress> userAddressList;

    // 交易号
    private String tradeNo;

}
