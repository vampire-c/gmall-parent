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
 * 订单表 订单表
 * @TableName order_info
 */
@TableName(value ="order_info")
@Data
public class OrderInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 收件人电话
     */
    private String consigneeTel;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 付款方式
     */
    private String paymentWay;

    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 订单备注
     */
    private String orderComment;

    /**
     * 订单交易编号（第三方支付用)
     */
    private String outTradeNo;

    /**
     * 订单描述(第三方支付用)
     */
    private String tradeBody;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 失效时间
     */
    private Date expireTime;

    /**
     * 订单处理状态
     */
    private String processStatus;

    /**
     * 物流单编号
     */
    private String trackingNo;

    /**
     * 父订单编号
     */
    private Long parentOrderId;

    /**
     * 图片路径
     */
    private String imgUrl;

    /**
     * 省id
     */
    private Long provinceId;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 促销金额
     */
    private BigDecimal activityReduceAmount;

    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;

    /**
     * 原价金额
     */
    private BigDecimal originalTotalAmount;

    /**
     * 运费
     */
    private BigDecimal feightFee;

    /**
     * 可退款日期（签收后30天）
     */
    private Date refundableTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}