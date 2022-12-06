package com.atguigu.gmall.util.consts;


public class MqConst {
    // 订单事件交换机
    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";
    // 订单延迟队列
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    // 订单延时队列时间 30分钟
    public static final Long ORDER_DELAY_TTL = 1800000L;
    // 订单路由键
    public static final String RK_ORDER_CREATED = "order.created";
    // 订单死信路由键
    public static final String RK_ORDER_DELAY = "order.delay";
    // 订单关闭队列
    public static final String ORDER_CLOSE_QUEUE = "order.close.queue";
    // 支付成功订单队列
    public static final String ORDER_PAYED_QUEUE = "order.payed.queue";
    // 已支付路由键
    public static final String RK_ORDER_PAYED = "order.payed";
    // 库存系统减库存交换机
    public static final String WARE_STOCK_EXCHANGE = "exchange.direct.ware.stock";
    // 库存系统减库存路由键
    public static final String RK_WARE_STOCK = "ware.stock";
    // 感知库存构建队列
    public static final String QUEUE_WARE_ORDER = "queue.ware.order";
    // 感知库存扣减交换机
    public static final String WARE_ORDER_EXCHANGE = "exchange.direct.ware.order";
    // 感知库存扣减路由键
    public static final String RK_WARE_ORDER = "ware.order";
}
