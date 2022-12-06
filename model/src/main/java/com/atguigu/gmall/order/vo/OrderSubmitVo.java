package com.atguigu.gmall.order.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@Data
public class OrderSubmitVo {

    // 收货人
    @NotBlank(message = "收货人不能为空")
    private String consignee;

    // 电话
    @NotBlank(message = "电话不能为空")
    @Length(min = 5, max = 15, message = "号码长度异常")
    private String consigneeTel;

    // 地址
    @NotBlank(message = "地址不能为空")
    private String deliveryAddress;

    // 备注
    private String orderComment;

    // 付款方式
    @NotBlank(message = "付款方式不能为空")
    private String paymentWay = "online";

    // 订单
    private List<DetailVo> orderDetailList;



    // @NoArgsConstructor
    // @Data
    // public static class OrderDetailListDTO {
    //
    //     private Long skuId;
    //
    //     private String skuName;
    //
    //     private Integer skuNum;
    //
    //     private BigDecimal orderPrice;
    //
    //     private String imgUrl;
    //
    // }
}

