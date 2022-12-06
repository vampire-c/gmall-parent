package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.order.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Anonymous
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2022-10-23 20:47:27
* @Entity com.atguigu.gmall.order.entity.OrderDetail
*/
@Repository
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




