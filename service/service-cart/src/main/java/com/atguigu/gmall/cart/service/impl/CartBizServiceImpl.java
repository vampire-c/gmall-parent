package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.service.CartBizService;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.user.vo.UserAuthInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartBizServiceImpl implements CartBizService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 添加商品到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public AddCartSuccessVo addToCart(Long skuId, Integer skuNum) {
        /*
        // 根据线程绑定机制,和 spring会自动把当前正在处理的请求共享到当前线程 获取 userInfoId, userTempId
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userInfoId = request.getHeader(RedisConst.USER_ID_HEADER);
        String userTempId = request.getHeader(RedisConst.USER_TEMP_ID_HEADER);
        */

        // 决定购物车使用的cartKey
        String cartKey = determineCartKey();
        // 给cartKey购物车中添加skuId,skuNum 商品
        SkuInfo skuInfo = addCartItem(cartKey, skuId, skuNum);
        return new AddCartSuccessVo(skuInfo, skuNum);
    }


    /**
     * 给指定购物车添加指定商品
     *
     * @param cartKey
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public SkuInfo addCartItem(String cartKey, Long skuId, Integer skuNum) {
        SkuInfo skuInfo = null;
        // 获取redis中的指定cartKey的购物车
        BoundHashOperations<String, String, String> cart = getCart(cartKey);
        // 判断购物车中是否存在该商品
        if (cart.hasKey(skuId.toString())) {
            // 有 数量增加
            // 修改购物车中商品数量
            CartItem cartItem = updateCartItemNum(skuId, skuNum);
            /*
            // 获取购物车中的商品
            CartItem cartItem = getCartItem(cartKey, skuId);
            // 增加数量
            cartItem.setSkuNum(cartItem.getSkuNum() + skuNum);
            // 更新购物车中的价格
            cartItem.setSkuPrice(skuDetailFeignClient.getSkuInfoPrice(skuId).getData());
            // 保存商品新数据
            cart.put(skuId.toString(), Jsons.toString(cartItem));
            */
            return convertCartItem2SkuInfo(cartItem);
        } else {
            // 无 新增
            //
            if (cart.size() >= RedisConst.CART_SIZE) {
                throw new GmallException(ResultCodeEnum.CART_SIZE_OVERLIMIT);
            }
            // 查询商品
            skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
            CartItem cartItem = convertSkuInfo2CartItem(skuInfo);
            cartItem.setSkuNum(skuNum);
            // 保存商品到购物车
            // cart.put(skuId.toString(), Jsons.toString(cartItem));
            saveCartItem(cartItem, cartKey);
            return skuInfo;
        }
    }


    /**
     * 从redis获取购物车中的商品
     *
     * @param cartKey
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(String cartKey, Long skuId) {
        // 从redis获取购物车数据
        String json = redisTemplate.opsForHash().get(cartKey, skuId.toString()).toString();
        if (!StringUtils.isEmpty(json)) {
            return Jsons.toObject(json, CartItem.class);
        }
        return null;
    }

    /**
     * 获取购物车列表
     *
     * @param cartKey
     * @return
     */
    @Override
    public List<CartItem> getCartItemList(String cartKey) {
        // 获取购物车
        BoundHashOperations<String, String, String> cart = getCart(cartKey);
        List<CartItem> cartItemList = cart.values() // 获取json集合
                .stream().map(json -> {
                    // 遍历集合将json转换商品对象
                    CartItem cartItem = Jsons.toObject(json, CartItem.class);
                    // 赋值实时价格
                    // cartItem.setSkuPrice(getCartItem1010Price(cartItem.getSkuId()));
                    return cartItem;
                })
                // 购物车中的商品按照时间排序
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        // 启动异步任务
        CompletableFuture.runAsync(() -> {
            // 共享旧线程
            RequestContextHolder.setRequestAttributes(attributes);
            // 更新商品价格
            updateCartItemPrice(cartKey, cartItemList);
            // 移除
            RequestContextHolder.resetRequestAttributes();
        }, threadPoolExecutor);

        return cartItemList;
    }

    /**
     * 更新商品价格
     *
     * @param cartKey
     * @param cartItemList
     */
    private void updateCartItemPrice(String cartKey, List<CartItem> cartItemList) {
        cartItemList.stream().forEach(cartItem -> {
            log.info("后台更新...商品{}价格:{}", cartItem.getSkuId(), cartItem.getSkuPrice());
            saveCartItem(cartItem, cartKey);
        });
    }

    /**
     * 修改购物车中商品数量
     *
     * @param skuId
     * @param skuNum
     */
    @Override
    public CartItem updateCartItemNum(Long skuId, Integer skuNum) {
        // 获取购物车名
        String cartKey = determineCartKey();
        // 获取待修改的商品
        CartItem cartItem = getCartItem(cartKey, skuId);
        // 修改商品数量
        cartItem.setSkuNum(cartItem.getSkuNum() + skuNum);
        //
        if (cartItem.getSkuNum() >= RedisConst.CART_ITEM_LENGTH) {
            throw new GmallException(ResultCodeEnum.CART_ITEM_OVERLIMIT);
        }
        // 保存修改后的商品到购物车
        saveCartItem(cartItem, cartKey);
        return cartItem;
    }

    /**
     * 保存商品到购物车,更新商品价格
     *
     * @param cartItem
     * @param cartKey
     */
    @Override
    public void saveCartItem(CartItem cartItem, String cartKey) {
        // 更新商品价格
        cartItem.setSkuPrice(skuDetailFeignClient.getSkuInfoPrice(cartItem.getSkuId()).getData());
        // 获取购物车,保存商品到购物车
        getCart(cartKey).put(cartItem.getSkuId().toString(), Jsons.toString(cartItem));
    }

    /**
     * 修改选中状态
     *
     * @param skuId
     * @param checkStatus
     */
    @Override
    public void checkCartItem(Long skuId, Integer checkStatus) {
        // 获取购物车名
        String cartKey = determineCartKey();
        CartItem cartItem = getCartItem(cartKey, skuId);
        // 修改选中状态
        cartItem.setIsChecked(checkStatus);
        // 保存数据
        saveCartItem(cartItem, cartKey);
    }

    /**
     * 删除购物车中的商品
     *
     * @param skuId
     */
    @Override
    public void deleteCartItem(Long skuId) {
        String cartKey = determineCartKey();
        // 删除购物车中是商品
        getCart(cartKey).delete(skuId.toString());
    }

    /**
     * 删除购物车选中的商品
     */
    @Override
    public void deleteChecked() {
        String cartKey = determineCartKey();
        // 获取购物车中选中的商品集合
        List<CartItem> checkedCartItemList = getCheckedCartItemList(cartKey);
        if (checkedCartItemList.size() > 0) {
            // 获取选中商品集合的skuId数组
            String[] skuIds = new String[checkedCartItemList.size()];
            for (int i = 0; i < checkedCartItemList.size(); i++) {
                skuIds[i] = checkedCartItemList.get(i).getSkuId().toString();
            }
            log.info("批量删除...{}", skuIds.length);
            // 批量删除
            BoundHashOperations<String, String, String> cart = getCart(cartKey);
            cart.delete(skuIds);
        }
    }

    /**
     * 获取购物车中选中的商品集合
     *
     * @param cartKey
     * @return
     */
    @Override
    public List<CartItem> getCheckedCartItemList(String cartKey) {
        // 获取商品集合
        List<CartItem> cartItemList = getCartItemList(cartKey);
        List<CartItem> checkedCartItemList = cartItemList.stream()
                .filter(cartItem -> cartItem.getIsChecked() == 1) // 过滤保留isChecked=1(选中的商品)
                .collect(Collectors.toList());
        return checkedCartItemList;
    }

    /**
     * 合并购物车
     *
     * @return
     */
    @Override
    public List<CartItem> mergeCart() {
        // 获取当前用户信息
        UserAuthInfoVo userAuthInfoVo = UserAuthUtils.getUserAuthInfo();
        Long userInfoId = userAuthInfoVo.getUserInfoId();
        String userTempId = userAuthInfoVo.getUserTempId();

        // 临时购物车名
        String tempCartKey = RedisConst.CART_INFO + userTempId;
        // 临时购物车所有数据
        List<CartItem> tempCartItemList = getCartItemList(tempCartKey);
        // 用户购物车名
        String userCartKey = RedisConst.CART_INFO + userInfoId;
        // 用户登录 && 临时购物车有商品 -> 合并购物车
        if (!StringUtils.isEmpty(userInfoId) && !StringUtils.isEmpty(tempCartItemList) && tempCartItemList.size() > 0) {
            // 将临时购物车的数据保存到用户购物车
            log.info("将临时购物车的数据保存到用户购物车");
            for (CartItem tempCartItem : tempCartItemList) {
                addCartItem(userCartKey, tempCartItem.getSkuId(), tempCartItem.getSkuNum());
            }
            // 清空临时购物车
            clearCart(tempCartKey);
        }
        // 用户购物车所有数据
        return getCartItemList(userCartKey);
    }

    /**
     * 清空购物车
     *
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey) {
        log.info("清空购物车");
        redisTemplate.delete(cartKey);
    }

    /**
     * 获取商品的实时(1010)价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getCartItem1010Price(Long skuId) {
        String price = redisTemplate.opsForValue().get(RedisConst.SKU_PRICE + skuId);
        return new BigDecimal(price);
    }

    /**
     * 给临时购物车添加过期时间
     *
     * @param cartKey
     */
    @Override
    public void expireTempCart(String cartKey) {
        // 判断该购物车的是否存在过期时间
        // -1 永不过期
        if (-1 == redisTemplate.getExpire(RedisConst.CART_INFO + cartKey)) {
            log.info("给临时购物车添加过期时间,{}", RedisConst.TEMP_CART_TTL + RedisConst.TTL_UNIT_DAYS.toString());
            redisTemplate.expire(cartKey, RedisConst.TEMP_CART_TTL, RedisConst.TTL_UNIT_DAYS);
        }
    }


    /**
     * 将购物车中的对象转为商品对象
     *
     * @param cartItem
     * @return
     */
    private SkuInfo convertCartItem2SkuInfo(CartItem cartItem) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(cartItem.getId());
        // skuInfo.setSpuId();
        skuInfo.setPrice(cartItem.getSkuPrice());
        skuInfo.setSkuName(cartItem.getSkuName());
        // skuInfo.setSkuDesc();
        // skuInfo.setWeight();
        // skuInfo.setTmId();
        // skuInfo.setCategory3Id();
        skuInfo.setSkuDefaultImg(cartItem.getSkuDefaultImg());
        // skuInfo.setIsSale();
        // skuInfo.setSkuImageList();
        return skuInfo;
    }


    /**
     * 将查询到的商品对象转换为购物车中保存的对象
     *
     * @param skuInfo
     * @return
     */
    private CartItem convertSkuInfo2CartItem(SkuInfo skuInfo) {
        CartItem cartItem = new CartItem();
        cartItem.setId(skuInfo.getId());
        cartItem.setSkuId(skuInfo.getId());
        cartItem.setCartPrice(skuInfo.getPrice());
        cartItem.setSkuPrice(skuInfo.getPrice());
        // cartItem.setSkuNum(0);
        cartItem.setSkuDefaultImg(skuInfo.getSkuDefaultImg());
        cartItem.setSkuName(skuInfo.getSkuName());
        cartItem.setIsChecked(1);
        cartItem.setCreateTime(new Date());
        cartItem.setUpdateTime(new Date());
        return cartItem;
    }


    /**
     * 根据指定的cartKey获取购物车
     *
     * @param cartKey
     * @return
     */
    private BoundHashOperations<String, String, String> getCart(String cartKey) {
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        return cart;
    }


    /**
     * 决定购物车使用的cartKey
     *
     * @return
     */
    @Override
    public String determineCartKey() {
        UserAuthInfoVo userAuthInfoVo = UserAuthUtils.getUserAuthInfo();
        Long userInfoId = userAuthInfoVo.getUserInfoId();
        String userTempId = userAuthInfoVo.getUserTempId();
        String cartKey = RedisConst.CART_INFO;
        if (StringUtils.isEmpty(userInfoId)) {
            cartKey += userTempId;
        } else {
            cartKey += userInfoId;
        }
        return cartKey;
    }
}
