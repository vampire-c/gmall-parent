package com.atguigu.gmall.order.biz.impl;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.entity.OrderStatusLog;
import com.atguigu.gmall.order.entity.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.order.to.OrderMsgTo;
import com.atguigu.gmall.order.vo.DetailVo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.util.consts.MqConst;
import com.atguigu.gmall.ware.WareStockMsg;
import com.atguigu.gmall.ware.WareStockResultMsg;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderBizServiceImpl implements OrderBizService {


    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderStatusLogService orderStatusLogService;

    @Autowired
    private WareFeignClient wareFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 获取订单结算数据
     *
     * @return
     */
    @Override
    public OrderConfirmVo getOrderConfirmData() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        // 封装各属性

        // 获取选中的商品集合
        List<CartItem> cartItemList = cartFeignClient.getCheckCartItems().getData();
        // 封装页面需要的对象属性集合
        List<DetailVo> detailVoList = cartItemList.stream()
                .map(cartItem -> {
                    DetailVo detailVo = new DetailVo();
                    // 商品id
                    detailVo.setSkuId(cartItem.getSkuId());
                    // 商品名
                    detailVo.setSkuName(cartItem.getSkuName());
                    // 商品数量
                    detailVo.setSkuNum(cartItem.getSkuNum());
                    // 商品价格
                    detailVo.setOrderPrice(cartItem.getSkuPrice());
                    // 默认图片
                    detailVo.setImgUrl(cartItem.getSkuDefaultImg());
                    // 是否有库存
                    detailVo.setHasStock(wareFeignClient.hasStock(cartItem.getSkuId(), cartItem.getSkuNum()));
                    return detailVo;
                }).collect(Collectors.toList());
        orderConfirmVo.setDetailArrayList(detailVoList);

        // 订单商品总数量
        Integer totalNum = detailVoList.stream()
                .map(detailVo1 -> {
                    //每种商品的数量
                    return detailVo1.getSkuNum();
                })
                // 累加
                .reduce((o1, o2) -> o1 + o2).get();
        orderConfirmVo.setTotalNum(totalNum);

        // 总价格
        BigDecimal totalAmount = detailVoList.stream()
                .map(detailVo -> {
                    // 每种商品的总价
                    Integer skuNum = detailVo.getSkuNum();
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    return orderPrice.multiply(new BigDecimal(skuNum.toString()));
                })
                //累加
                .reduce((o1, o2) -> o1.add(o2)).get();
        orderConfirmVo.setTotalAmount(totalAmount);

        // 用户收货地址列表
        List<UserAddress> userAddressList = userFeignClient.getUserAddressList().getData();
        orderConfirmVo.setUserAddressList(userAddressList);

        // 交易号
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setTradeNo(tradeNo);
        // 给redis存储一份用于重复请求校验
        redisTemplate.opsForValue().set(RedisConst.ORDER_TRADE_NO + tradeNo, RedisConst.TEMP_DATA, RedisConst.ORDER_TRADE_NO_TTL, RedisConst.TTL_UNIT_MINUTES);

        return orderConfirmVo;
    }

    /**
     * 提交订单
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        // 重复请求校验
        /*
        Boolean hasKey = redisTemplate.hasKey(RedisConst.ORDER_TRADE_NO + tradeNo);
        if (hasKey) {
            //

            redisTemplate.delete(RedisConst.ORDER_TRADE_NO + tradeNo);
        } else {
            // 重复的请求或假请求
            throw new GmallException(ResultCodeEnum.REQ_REPEAT);
        }
         */

        String script = "if redis.call(\"exists\",KEYS[1])\n" +
                "then\n" +
                "   return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "   return 0\n" +
                "end";
        Long aLong = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(RedisConst.ORDER_TRADE_NO + tradeNo));
        if (0 == aLong) {
            // 重复的请求或假请求
            log.info("重复的请求或非法请求");
            throw new GmallException(ResultCodeEnum.REQ_REPEAT);
        }
        /*
        // 重复请求校验
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */

        // 库存校验
        List<DetailVo> NoHasStockDetailVoList = orderSubmitVo.getOrderDetailList().stream()
                .filter(detailVo -> {
                    // 库存校验
                    String stock = wareFeignClient.hasStock(detailVo.getSkuId(), detailVo.getSkuNum());
                    return "0".equals(stock);
                }).collect(Collectors.toList());
        if (!StringUtils.isEmpty(NoHasStockDetailVoList) && NoHasStockDetailVoList.size() > 0) {
            // 商品价格发生异常
            log.info("商品库存异常: {}", NoHasStockDetailVoList);
            throw new GmallException(ResultCodeEnum.SKU_HAS_NO_STOCK);
        }


        // 商品价格校验
        List<DetailVo> detailVoList = orderSubmitVo.getOrderDetailList().stream()
                .filter(detailVo -> {
                    // 订单中的价格
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    // 数据库中的价格
                    BigDecimal skuInfoPrice = skuDetailFeignClient.getSkuInfoPrice(detailVo.getSkuId()).getData();
                    // 返回价额有差异的商品
                    return !skuInfoPrice.equals(orderPrice);
                }).collect(Collectors.toList());
        if (!StringUtils.isEmpty(detailVoList) && detailVoList.size() > 0) {
            // 商品价格发生异常
            log.info("商品价格异常: {}", detailVoList);
            throw new GmallException(ResultCodeEnum.SKU_PRICE_CHANGE);
        }

        // 保存订单到数据库
        Long orderId = saveOrder(tradeNo, orderSubmitVo);

        // 移除购物车中选中的商品
        cartFeignClient.deleteChecked();
        log.info("保存订单,移除购物车中商品");

        // 30分钟不支付就关闭订单
        Long userId = UserAuthUtils.getUserAuthInfo().getUserInfoId();
        // 延时任务
        /*
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        scheduledExecutorService.schedule(() -> {
            closeOrder(orderId, userId);
        }, 30, TimeUnit.MINUTES);
        */
        // 延时MQ消息
        OrderMsgTo orderMsgTo = new OrderMsgTo(orderId, userId);
        rabbitTemplate.convertAndSend(
                MqConst.ORDER_EVENT_EXCHANGE,
                MqConst.RK_ORDER_CREATED,
                Jsons.toString(orderMsgTo));
        log.info("发送30分钟后关闭订单的MQ:{}", orderMsgTo);

        // 返回订单id
        return orderId;
    }

    /**
     * 保存订单
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @Override
    public Long saveOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        // 保存 order_info 订单
        OrderInfo orderInfo = saveOrderInfo(tradeNo, orderSubmitVo);
        // 保存 order_detail 订单明细
        saveOrderDetail(orderSubmitVo, orderInfo);
        // 保存 order_status_log 操作日志
        saveOrderStatusLog(orderInfo);
        return orderInfo.getId();
    }

    @Scheduled(cron = "0 */30 * ? * *")
    private void scanAndCloserOrder() {

    }


    /**
     * 关闭订单
     *
     * @param orderId
     */
    @Override
    public void closeOrder(Long orderId, Long userId) {
        changeOrderStatusByCAS(
                Arrays.asList(OrderStatus.UNPAID),
                Arrays.asList(ProcessStatus.UNPAID),
                OrderStatus.CLOSED,
                ProcessStatus.CLOSED,
                orderId,
                userId);
    }

    /**
     * 修改订单详细状态
     *
     * @param expectOrderStatusList   期望订单状态集合(满足任一即可)
     * @param expectProcessStatusList 期望订单处理状态集合(满足任一即可)
     * @param orderStatus             修改成的订单状态
     * @param processStatus           修改成的订单处理状态
     * @param orderId                 订单id
     * @param userId                  用户id
     */
    @Override
    public void changeOrderStatusByCAS(List<OrderStatus> expectOrderStatusList,
                                       List<ProcessStatus> expectProcessStatusList,
                                       OrderStatus orderStatus,
                                       ProcessStatus processStatus,
                                       Long orderId,
                                       Long userId) {
        // 期望订单状态集合
        List<String> orderStatusList = expectOrderStatusList.stream()
                .map(expectOrderStatus -> expectOrderStatus.name()).collect(Collectors.toList());

        // 期望订单处理状态集合
        List<String> processStatusList = expectProcessStatusList.stream()
                .map(expectProcessStatus -> expectProcessStatus.name()).collect(Collectors.toList());

        orderInfoMapper.updateOrderStatusByCAS(
                orderStatusList,
                processStatusList,
                orderStatus.name(),
                processStatus.name(),
                orderId,
                userId);

    }

    /**
     * 根据订单id和用户id查询订单详情
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public OrderInfo getOrderInfoByOrderIdAndUserId(Long orderId, Long userId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getUserId, userId);
        OrderInfo orderInfo = orderInfoService.getOne(queryWrapper);
        return orderInfo;
    }

    /**
     * 修改订单为已支付
     *
     * @param json
     */
    @Override
    public void updateOrderStatusPayed(String json) {
        // 获取需要修改的订单的内容
        Map<String, String> content = Jsons.toObject(json, new TypeReference<Map<String, String>>() {
        });

        // 保存支付信息到数据库
        PaymentInfo paymentInfo = preparePaymentInfo(content);
        paymentInfoService.save(paymentInfo);

        // 修改订单状态为已支付
        changeOrderStatusByCAS(
                Arrays.asList(OrderStatus.CLOSED, OrderStatus.UNPAID),
                Arrays.asList(ProcessStatus.CLOSED, ProcessStatus.UNPAID),
                OrderStatus.PAID,
                ProcessStatus.PAID,
                Long.parseLong(paymentInfo.getOrderId()),
                paymentInfo.getUserId()
        );
        log.info("修改订单状态为已支付");

        // 调用库存系统,扣减库存
        // sendStockMsg();
        WareStockMsg wareStockMsg = properWareStockMsg(Long.parseLong(paymentInfo.getOrderId()), paymentInfo.getUserId());

        rabbitTemplate.convertAndSend(
                MqConst.WARE_STOCK_EXCHANGE,
                MqConst.RK_WARE_STOCK,
                Jsons.toString(wareStockMsg));


    }

    /**
     * 感知库存扣减, 修改订单状态
     *
     * @param wareStockResultMsg
     */
    @Override
    public void updateOrderStatusByStockResult(WareStockResultMsg wareStockResultMsg) {
        Long orderId = wareStockResultMsg.getOrderId();
        String status = wareStockResultMsg.getStatus();
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId);
        OrderInfo orderInfo = orderInfoService.getOne(queryWrapper);
        //

        ProcessStatus processStatus = null;
        OrderStatus orderStatus = null;
        switch (status) {
            case "DEDUCTED":
                processStatus = ProcessStatus.NOTIFIED_WARE;
                orderStatus = OrderStatus.WAITING_DELEVER;
            case "OUT_OF_STOCK":
                processStatus = ProcessStatus.STOCK_EXCEPTION;
                orderStatus = OrderStatus.WAITING_SCHEDULE;
        }
        changeOrderStatusByCAS(
                Arrays.asList(OrderStatus.PAID),
                Arrays.asList(ProcessStatus.PAID),
                orderStatus,
                processStatus,
                orderId,
                orderInfo.getUserId()
        );
    }

    private WareStockMsg properWareStockMsg(Long orderId, Long userId) {
        OrderInfo orderInfo = getOrderInfoByOrderIdAndUserId(orderId, userId);
        WareStockMsg wareStockMsg = new WareStockMsg();
        // 订单id
        wareStockMsg.setOrderId(orderId);
        // 收货人
        wareStockMsg.setConsignee(orderInfo.getConsignee());
        // 收货电话
        wareStockMsg.setConsigneeTel(orderInfo.getConsigneeTel());
        // 订单备注
        wareStockMsg.setOrderComment(orderInfo.getOrderComment());
        // 订单概要
        wareStockMsg.setOrderBody(orderInfo.getTradeBody());
        // 发货地址
        wareStockMsg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        // 支付方式
        wareStockMsg.setPaymentWay("2");
        // 购买商品明细
        List<OrderDetail> orderDetailList = orderDetailService.getOrderDetails(orderId, userId);
        List<WareStockMsg.DetailSku> detailSkuList = orderDetailList.stream()
                .map(orderDetail -> {
                    // 封装为所需对象集合
                    WareStockMsg.DetailSku detailSku = new WareStockMsg.DetailSku();
                    detailSku.setSkuId(orderDetail.getSkuId());
                    detailSku.setSkuNum(orderDetail.getSkuNum());
                    detailSku.setSkuName(orderDetail.getSkuName());
                    return detailSku;
                }).collect(Collectors.toList());
        wareStockMsg.setDetails(detailSkuList);
        return wareStockMsg;
    }

    /**
     * 封装支付信息
     *
     * @param content
     * @return
     */
    private PaymentInfo preparePaymentInfo(Map<String, String> content) {
        PaymentInfo paymentInfo = new PaymentInfo();
        // 对外业务编号
        paymentInfo.setOutTradeNo(content.get("out_trade_no"));
        OrderInfo orderInfo = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getOutTradeNo, paymentInfo.getOutTradeNo()));
        // 用户id
        paymentInfo.setUserId(orderInfo.getUserId());
        // 订单编号
        paymentInfo.setOrderId(orderInfo.getId().toString());
        // 支付类型
        paymentInfo.setPaymentType("ALIPAY");
        // 交易编号
        paymentInfo.setTradeNo(content.get("trade_no"));
        // 支付金额
        paymentInfo.setTotalAmount(new BigDecimal(content.get("total_amount")));
        // 交易内容
        paymentInfo.setSubject(content.get("subject"));
        // 支付状态
        paymentInfo.setPaymentStatus(content.get("trade_status"));
        // 创建时间
        paymentInfo.setCreateTime(new Date());
        // 回调时间
        paymentInfo.setCallbackTime(new Date());
        // 回调信息
        paymentInfo.setCallbackContent(Jsons.toString(content));
        return paymentInfo;
    }


    /**
     * 保存 order_info
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    private OrderInfo saveOrderInfo(String tradeNo, OrderSubmitVo orderSubmitVo) {
        OrderInfo orderInfo = new OrderInfo();
        // 收货人
        orderInfo.setConsignee(orderSubmitVo.getConsignee());
        // 电话号
        orderInfo.setConsigneeTel(orderSubmitVo.getConsigneeTel());
        // 订单总价
        BigDecimal totalAmount = orderSubmitVo.getOrderDetailList().stream()
                .map(detailVo -> {
                    // 返回每种商品的总价
                    Integer skuNum = detailVo.getSkuNum();
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    return orderPrice.multiply(new BigDecimal(skuNum.toString()));
                })
                // 累加
                .reduce((o1, o2) -> o1.add(o2)).get();
        orderInfo.setTotalAmount(totalAmount);
        // 订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        // 用户id
        orderInfo.setUserId(UserAuthUtils.getUserAuthInfo().getUserInfoId());
        // 付款方式
        orderInfo.setPaymentWay(orderSubmitVo.getPaymentWay());
        // 送货地址
        orderInfo.setDeliveryAddress(orderSubmitVo.getDeliveryAddress());
        // 订单备注
        orderInfo.setOrderComment(orderSubmitVo.getOrderComment());
        // 订单交易编号
        orderInfo.setOutTradeNo(tradeNo);
        // 订单描述(默认第一个商品名)
        orderInfo.setTradeBody(orderSubmitVo.getOrderDetailList().get(0).getSkuName());
        // 创建时间
        orderInfo.setCreateTime(new Date());
        // 失效时间(30分)
        orderInfo.setExpireTime(new Date(System.currentTimeMillis() + RedisConst.ORDER_TTL));
        // 订单处理状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        // orderInfo.setTrackingNo(); // 物流单编号
        // orderInfo.setParentOrderId(); // 父订单编号
        // 图片(默认第一个商品的图片)
        orderInfo.setImgUrl(orderSubmitVo.getOrderDetailList().get(0).getImgUrl());
        // orderInfo.setProvinceId(); // 省id
        // 操作时间
        orderInfo.setOperateTime(new Date());
        // orderInfo.setActivityReduceAmount(); // 促销金额
        // orderInfo.setCouponAmount(); // 优惠券金额
        // 原价金额
        orderInfo.setOriginalTotalAmount(totalAmount);
        // 运费(免运费)
        orderInfo.setFeightFee(new BigDecimal("0"));
        // orderInfo.setRefundableTime(); // 可退款日期
        orderInfoService.save(orderInfo);
        return orderInfo;
    }


    /**
     * 保存 order_detail 订单明细
     *
     * @param orderSubmitVo
     * @param orderInfo
     */
    private void saveOrderDetail(OrderSubmitVo orderSubmitVo, OrderInfo orderInfo) {
        // 封装为数据库所对应的对象
        List<OrderDetail> orderDetailList = orderSubmitVo.getOrderDetailList().stream()
                .map(detailVo -> {
                    OrderDetail orderDetail = new OrderDetail();
                    // 订单id
                    orderDetail.setOrderId(orderInfo.getId());
                    // 商品id
                    orderDetail.setSkuId(detailVo.getSkuId());
                    // 用户id
                    orderDetail.setUserId(orderInfo.getUserId());
                    // 商品名称
                    orderDetail.setSkuName(detailVo.getSkuName());
                    // 商品图片
                    orderDetail.setImgUrl(detailVo.getImgUrl());
                    // 商品价格
                    orderDetail.setOrderPrice(detailVo.getOrderPrice());
                    // 商品数量
                    orderDetail.setSkuNum(detailVo.getSkuNum());
                    // 创建时间
                    orderDetail.setCreateTime(new Date());
                    // 实际支付金额
                    orderDetail.setSplitTotalAmount(detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNum())));
                    // orderDetail.setSplitActivityAmount(); // 促销分摊金额
                    // orderDetail.setSplitCouponAmount(); // 优惠券分摊金额
                    return orderDetail;
                }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetailList);

    }


    /**
     * 保存 order_status_log
     *
     * @param orderInfo
     */
    private void saveOrderStatusLog(OrderInfo orderInfo) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        // 订单id
        orderStatusLog.setOrderId(orderInfo.getId());
        // 用户id
        orderStatusLog.setUserId(orderInfo.getUserId());
        // 订单状态
        orderStatusLog.setOrderStatus(orderInfo.getOrderStatus());
        // 修改时间
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogService.save(orderStatusLog);
    }
}
