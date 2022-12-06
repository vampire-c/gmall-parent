package com.atguigu.gmall.ware;

import lombok.Data;

@Data
public class WareStockResultMsg {
    // 订单id
    private Long orderId;
    // 订单扣减状态
    private String status;
}
