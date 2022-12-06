package com.atguigu.gmall.order.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class OrderMsgTo {
    private Long orderId;
    private Long userId;
}
