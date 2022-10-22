package com.atguigu.gmall.cart.service;


import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.product.entity.SkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface CartBizService {

    /**
     * 添加商品到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    AddCartSuccessVo addToCart(Long skuId, Integer skuNum);

    /**
     * 决定购物车使用的cartKey
     *
     * @return
     */
    public String determineCartKey();

    /**
     * 给指定购物车添加指定商品
     *
     * @param cartKey
     * @param skuId
     * @param skuNum
     * @return
     */
    public SkuInfo addCartItem(String cartKey, Long skuId, Integer skuNum);

    /**
     * 从redis获取购物车中的商品
     *
     * @param cartKey
     * @param skuId
     * @return
     */
    public CartItem getCartItem(String cartKey, Long skuId);

    /**
     * 获取购物车列表
     *
     * @param cartKey
     * @return
     */
    List<CartItem> getCartItemList(String cartKey);

    /**
     * 修改购物车中商品数量
     *
     * @param skuId
     * @param skuNum
     */
    CartItem updateCartItemNum(Long skuId, Integer skuNum);

    /**
     * 保存商品到购物车
     *
     * @param cartItem
     * @param cartKey
     */
    void saveCartItem(CartItem cartItem, String cartKey);

    /**
     * 修改选中状态
     *
     * @param skuId
     * @param checkStatus
     */
    void checkCartItem(Long skuId, Integer checkStatus);

    /**
     * 删除购物车中是商品
     *
     * @param skuId
     */
    void deleteCartItem(Long skuId);

    /**
     * 删除购物车选中的商品
     */
    void deleteChecked();

    /**
     * 获取购物车中选中的商品集合
     *
     * @param cartKey
     * @return
     */
    public List<CartItem> getCheckedCartItemList(String cartKey);

    /**
     * 合并购物车
     *
     * @return
     */
    List<CartItem> mergeCart();

    /**
     * 清空购物车
     * @param cartKey
     */
    public void clearCart(String cartKey);

    /**
     * 获取商品的实时(1010)价格
     * @param skuId
     * @return
     */
    BigDecimal getCartItem1010Price(Long skuId);

    /**
     * 给临时购物车添加过期时间
     * @param cartKey
     */
    void expireTempCart(String cartKey);
}
