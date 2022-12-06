package com.atguigu.gmall.ware;

import lombok.Data;

import java.util.List;

@Data
public class WareStockMsg {

    // 订单id
    private Long orderId;
    // 收货人
    private String consignee;
    // 收货电话
    private String consigneeTel;
    // 订单备注
    private String orderComment;
    // 订单概要
    private String orderBody;
    // 发货地址
    private String deliveryAddress;
    // 支付方式  ‘1’ 为货到付款，‘2’为在线支付。
    private String paymentWay = "2";
    // 购买商品明细
    private List<DetailSku> details;

    // 购买商品明细
    @Data
    public static class DetailSku {
        private Long skuId;
        private Integer skuNum;
        private String skuName;
    }
}
