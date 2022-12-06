package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.order.entity.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Anonymous
* @description 针对表【payment_info(支付信息表)】的数据库操作Mapper
* @createDate 2022-10-23 20:47:27
* @Entity com.atguigu.gmall.order.entity.PaymentInfo
*/
@Repository
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {

}




