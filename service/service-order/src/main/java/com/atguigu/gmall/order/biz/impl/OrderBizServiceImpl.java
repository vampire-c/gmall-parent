package com.atguigu.gmall.order.biz.impl;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.entity.OrderStatusLog;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.vo.DetailVo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.user.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderStatusLogService orderStatusLogService;

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
                    detailVo.setSkuId(cartItem.getSkuId());
                    detailVo.setSkuName(cartItem.getSkuName());
                    detailVo.setSkuNum(cartItem.getSkuNum());
                    detailVo.setOrderPrice(cartItem.getSkuPrice());
                    detailVo.setImgUrl(cartItem.getSkuDefaultImg());
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
        // 重复校验
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
            throw new GmallException(ResultCodeEnum.REQ_REPEAT);
        }
        /*
        // 重复请求
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */

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
        return saveOrder(tradeNo, orderSubmitVo);
    }

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
