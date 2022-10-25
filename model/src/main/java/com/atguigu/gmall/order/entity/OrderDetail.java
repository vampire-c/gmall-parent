package com.atguigu.gmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细表
 * @TableName order_detail
 */
@TableName(value ="order_detail")
@Data
public class OrderDetail implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * sku_id
     */
    private Long skuId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * sku名称（冗余)
     */
    private String skuName;

    /**
     * 图片名称（冗余)
     */
    private String imgUrl;

    /**
     * 购买价格(下单时sku价格）
     */
    private BigDecimal orderPrice;

    /**
     * 购买个数
     */
    private Integer skuNum;

    /**
     * 操作时间
     */
    private Date createTime;

    /**
     * 实际支付金额
     */
    private BigDecimal splitTotalAmount;

    /**
     * 促销分摊金额
     */
    private BigDecimal splitActivityAmount;

    /**
     * 优惠券分摊金额
     */
    private BigDecimal splitCouponAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}