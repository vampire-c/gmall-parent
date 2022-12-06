package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.order.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Mapper
 * @createDate 2022-10-23 20:47:27
 * @Entity com.atguigu.gmall.order.entity.OrderInfo
 */
@Repository
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * @param expectOrderStatusList   期望订单状态集合(满足任一即可)
     * @param expectProcessStatusList 期望订单处理状态集合(满足任一即可)
     * @param orderStatus             修改成的订单状态
     * @param processStatus           修改成的订单处理状态
     * @param orderId                 订单id
     * @param userId                  用户id
     */
    void updateOrderStatusByCAS(@Param("expectOrderStatusList") List<String> expectOrderStatusList,
                                @Param("expectProcessStatusList") List<String> expectProcessStatusList,
                                @Param("orderStatus") String orderStatus,
                                @Param("processStatus") String processStatus,
                                @Param("orderId") Long orderId,
                                @Param("userId") Long userId);
}




