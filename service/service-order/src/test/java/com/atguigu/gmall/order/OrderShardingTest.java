package com.atguigu.gmall.order;

import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class OrderShardingTest {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Test
    public void testShardingQuery() {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(OrderInfo::getUserId, 4L);
        queryWrapper.eq(OrderInfo::getId, 1L);

        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(queryWrapper);
        orderInfoList.forEach(orderInfo -> System.out.println("Order: " + orderInfo));
    }


    /**
     * 保存
     */
    @Test
    public void testShardingSave() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setConsignee("张三");
        orderInfo.setConsigneeTel("123456");
        orderInfo.setTotalAmount(new BigDecimal("0"));
        orderInfo.setOrderStatus("");
        orderInfo.setUserId(3L);
        orderInfo.setPaymentWay("");
        orderInfo.setDeliveryAddress("");
        orderInfo.setOrderComment("");
        orderInfo.setOutTradeNo("");
        orderInfo.setTradeBody("");
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date());
        orderInfo.setProcessStatus("");
        orderInfo.setTrackingNo("");
        orderInfo.setParentOrderId(0L);
        orderInfo.setImgUrl("");
        orderInfo.setProvinceId(0L);
        orderInfo.setOperateTime(new Date());
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));
        orderInfo.setOriginalTotalAmount(new BigDecimal("0"));
        orderInfo.setFeightFee(new BigDecimal("0"));
        orderInfo.setRefundableTime(new Date());


        orderInfoMapper.insert(orderInfo);
    }
}
