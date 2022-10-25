package com.atguigu.gmall.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),

    PAY_RUN(205, "支付中"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    SECKILL_NO_START(210, "秒杀还没开始"),
    SECKILL_RUN(211, "正在排队中"),
    SECKILL_NO_PAY_ORDER(212, "您有未支付的订单"),
    SECKILL_FINISH(213, "已售罄"),
    SECKILL_END(214, "秒杀已结束"),
    SECKILL_SUCCESS(215, "抢单成功"),
    SECKILL_FAIL(216, "抢单失败"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SECKILL_ORDER_SUCCESS(218, "下单成功"),
    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕"),

    DATA_NOT_EXIST(30001, "数据不存在"),
    USER_PWD_INVAILD(40001, "用域名或密码错误"),
    CART_SIZE_OVERLIMIT(50001, "购物车品类数量超出限制"),
    CART_ITEM_OVERLIMIT(50002, "购物车单次购买商品数量超出限制"),
    ARGS_INVALIDE(10001, "提交的数据不合法"),
    REQ_REPEAT(30002, "重复的请求"),
    SKU_PRICE_CHANGE(30003, "商品价格异常");

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
