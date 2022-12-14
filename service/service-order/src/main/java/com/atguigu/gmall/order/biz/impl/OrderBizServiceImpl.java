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
     * ????????????????????????
     *
     * @return
     */
    @Override
    public OrderConfirmVo getOrderConfirmData() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        // ???????????????

        // ???????????????????????????
        List<CartItem> cartItemList = cartFeignClient.getCheckCartItems().getData();
        // ???????????????????????????????????????
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

        // ?????????????????????
        Integer totalNum = detailVoList.stream()
                .map(detailVo1 -> {
                    //?????????????????????
                    return detailVo1.getSkuNum();
                })
                // ??????
                .reduce((o1, o2) -> o1 + o2).get();
        orderConfirmVo.setTotalNum(totalNum);

        // ?????????
        BigDecimal totalAmount = detailVoList.stream()
                .map(detailVo -> {
                    // ?????????????????????
                    Integer skuNum = detailVo.getSkuNum();
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    return orderPrice.multiply(new BigDecimal(skuNum.toString()));
                })
                //??????
                .reduce((o1, o2) -> o1.add(o2)).get();
        orderConfirmVo.setTotalAmount(totalAmount);

        // ????????????????????????
        List<UserAddress> userAddressList = userFeignClient.getUserAddressList().getData();
        orderConfirmVo.setUserAddressList(userAddressList);

        // ?????????
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setTradeNo(tradeNo);
        // ???redis????????????????????????????????????
        redisTemplate.opsForValue().set(RedisConst.ORDER_TRADE_NO + tradeNo, RedisConst.TEMP_DATA, RedisConst.ORDER_TRADE_NO_TTL, RedisConst.TTL_UNIT_MINUTES);

        return orderConfirmVo;
    }

    /**
     * ????????????
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        // ????????????
        /*
        Boolean hasKey = redisTemplate.hasKey(RedisConst.ORDER_TRADE_NO + tradeNo);
        if (hasKey) {
            //

            redisTemplate.delete(RedisConst.ORDER_TRADE_NO + tradeNo);
        } else {
            // ???????????????????????????
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
            // ???????????????????????????
            throw new GmallException(ResultCodeEnum.REQ_REPEAT);
        }
        /*
        // ????????????
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */

        // ??????????????????
        List<DetailVo> detailVoList = orderSubmitVo.getOrderDetailList().stream()
                .filter(detailVo -> {
                    // ??????????????????
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    // ?????????????????????
                    BigDecimal skuInfoPrice = skuDetailFeignClient.getSkuInfoPrice(detailVo.getSkuId()).getData();
                    // ??????????????????????????????
                    return !skuInfoPrice.equals(orderPrice);
                }).collect(Collectors.toList());
        if (!StringUtils.isEmpty(detailVoList) && detailVoList.size() > 0) {
            // ????????????????????????
            log.info("??????????????????: {}", detailVoList);
            throw new GmallException(ResultCodeEnum.SKU_PRICE_CHANGE);
        }

        // ????????????????????????
        return saveOrder(tradeNo, orderSubmitVo);
    }

    @Override
    public Long saveOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        // ?????? order_info ??????
        OrderInfo orderInfo = saveOrderInfo(tradeNo, orderSubmitVo);
        // ?????? order_detail ????????????
        saveOrderDetail(orderSubmitVo, orderInfo);
        // ?????? order_status_log ????????????
        saveOrderStatusLog(orderInfo);
        return orderInfo.getId();
    }


    /**
     * ?????? order_info
     *
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    private OrderInfo saveOrderInfo(String tradeNo, OrderSubmitVo orderSubmitVo) {
        OrderInfo orderInfo = new OrderInfo();
        // ?????????
        orderInfo.setConsignee(orderSubmitVo.getConsignee());
        // ?????????
        orderInfo.setConsigneeTel(orderSubmitVo.getConsigneeTel());
        // ????????????
        BigDecimal totalAmount = orderSubmitVo.getOrderDetailList().stream()
                .map(detailVo -> {
                    // ???????????????????????????
                    Integer skuNum = detailVo.getSkuNum();
                    BigDecimal orderPrice = detailVo.getOrderPrice();
                    return orderPrice.multiply(new BigDecimal(skuNum.toString()));
                })
                // ??????
                .reduce((o1, o2) -> o1.add(o2)).get();
        orderInfo.setTotalAmount(totalAmount);
        // ????????????
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        // ??????id
        orderInfo.setUserId(UserAuthUtils.getUserAuthInfo().getUserInfoId());
        // ????????????
        orderInfo.setPaymentWay(orderSubmitVo.getPaymentWay());
        // ????????????
        orderInfo.setDeliveryAddress(orderSubmitVo.getDeliveryAddress());
        // ????????????
        orderInfo.setOrderComment(orderSubmitVo.getOrderComment());
        // ??????????????????
        orderInfo.setOutTradeNo(tradeNo);
        // ????????????(????????????????????????)
        orderInfo.setTradeBody(orderSubmitVo.getOrderDetailList().get(0).getSkuName());
        // ????????????
        orderInfo.setCreateTime(new Date());
        // ????????????(30???)
        orderInfo.setExpireTime(new Date(System.currentTimeMillis() + RedisConst.ORDER_TTL));
        // ??????????????????
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        // orderInfo.setTrackingNo(); // ???????????????
        // orderInfo.setParentOrderId(); // ???????????????
        // ??????(??????????????????????????????)
        orderInfo.setImgUrl(orderSubmitVo.getOrderDetailList().get(0).getImgUrl());
        // orderInfo.setProvinceId(); // ???id
        // ????????????
        orderInfo.setOperateTime(new Date());
        // orderInfo.setActivityReduceAmount(); // ????????????
        // orderInfo.setCouponAmount(); // ???????????????
        // ????????????
        orderInfo.setOriginalTotalAmount(totalAmount);
        // ??????(?????????)
        orderInfo.setFeightFee(new BigDecimal("0"));
        // orderInfo.setRefundableTime(); // ???????????????
        orderInfoService.save(orderInfo);
        return orderInfo;
    }


    /**
     * ?????? order_detail ????????????
     *
     * @param orderSubmitVo
     * @param orderInfo
     */
    private void saveOrderDetail(OrderSubmitVo orderSubmitVo, OrderInfo orderInfo) {
        // ????????????????????????????????????
        List<OrderDetail> orderDetailList = orderSubmitVo.getOrderDetailList().stream()
                .map(detailVo -> {
                    OrderDetail orderDetail = new OrderDetail();
                    // ??????id
                    orderDetail.setOrderId(orderInfo.getId());
                    // ??????id
                    orderDetail.setSkuId(detailVo.getSkuId());
                    // ??????id
                    orderDetail.setUserId(orderInfo.getUserId());
                    // ????????????
                    orderDetail.setSkuName(detailVo.getSkuName());
                    // ????????????
                    orderDetail.setImgUrl(detailVo.getImgUrl());
                    // ????????????
                    orderDetail.setOrderPrice(detailVo.getOrderPrice());
                    // ????????????
                    orderDetail.setSkuNum(detailVo.getSkuNum());
                    // ????????????
                    orderDetail.setCreateTime(new Date());
                    // ??????????????????
                    orderDetail.setSplitTotalAmount(detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNum())));
                    // orderDetail.setSplitActivityAmount(); // ??????????????????
                    // orderDetail.setSplitCouponAmount(); // ?????????????????????
                    return orderDetail;
                }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetailList);

    }


    /**
     * ?????? order_status_log
     *
     * @param orderInfo
     */
    private void saveOrderStatusLog(OrderInfo orderInfo) {
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        // ??????id
        orderStatusLog.setOrderId(orderInfo.getId());
        // ??????id
        orderStatusLog.setUserId(orderInfo.getUserId());
        // ????????????
        orderStatusLog.setOrderStatus(orderInfo.getOrderStatus());
        // ????????????
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogService.save(orderStatusLog);
    }
}
