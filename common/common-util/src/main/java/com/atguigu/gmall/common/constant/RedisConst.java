package com.atguigu.gmall.common.constant;

import java.util.concurrent.TimeUnit;

public class RedisConst {
    // skuInfo缓存前缀
    public static final String SKU_DETAIL_CACHE_PREFIX = "sku:info:";
    // 商品分类树
    public static final String CATEGORYS_CACHE = "categorys:tree";
    // 分布式锁前缀
    public static final String REDIS_LOCK_PREFIX = "lock-";
    // "x"
    public static final String TEMP_DATA = "x";
    // 时间单位 HOURS
    public static final TimeUnit TTL_UNIT_HOURS = TimeUnit.HOURS;
    // skuId布隆过滤器
    public static final String BLOOM_SKUID = "skuId-bloom";
    // 临时时间
    public static final Long TEMP_DATA_TTL = 7L;
    // 时间单位 DAYS
    public static final TimeUnit TTL_UNIT_DAYS = TimeUnit.DAYS;
    // 热度分
    public static final String HOTSCORE = "hotscore:";
    // 用户登录信息
    public static final String USER_LOGIN = "user:login:";
    // 用户临牌过期时间
    public static final Long USER_LOGIN_TTL = 7L;
    // 用户 id
    public static final String USER_ID_HEADER = "userInfoId";
    // 临时用户id
    public static final String USER_TEMP_ID_HEADER = "userTempId";
    // 购物车
    public static final String CART_INFO = "cart:info:";
    // 商品实时价格
    public static final String SKU_PRICE = "sku:price:";
    // 购物车上限
    public static final Long CART_SIZE = 100L;
    // 商品单次购买上限
    public static final Integer CART_ITEM_LENGTH = 100;
    // 临时购物车过期时间
    public static final long TEMP_CART_TTL = 30L;
    // 订单交易号
    public static final String ORDER_TRADE_NO = "order:tradeNo:";
    // 订单交易号过期时间
    public static final long ORDER_TRADE_NO_TTL = 30;
    // 时间单位 MINUTES
    public static final TimeUnit TTL_UNIT_MINUTES = TimeUnit.MINUTES;
    // 订单过期时间
    public static final long ORDER_TTL = 30 * 60 * 1000;
}
