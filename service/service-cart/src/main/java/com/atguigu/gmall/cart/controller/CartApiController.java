package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.service.CartBizService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.UserAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartBizService cartBizService;

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("/cartList")
    public Result cartList() {
        // 获取要查询的购物车名
        String cartKey = cartBizService.determineCartKey();
        // 获取购物车列表
        List<CartItem> cartItemList = cartBizService.getCartItemList(cartKey);
        // 判断userInfoId -> 用户是否登录
        if (!StringUtils.isEmpty(UserAuthUtils.getUserAuthInfo().getUserInfoId())) {
            // 登录-> 可能需要合并购物车
            cartItemList = cartBizService.mergeCart();
        } else {
            // 给临时购物车添加过期时间
            cartBizService.expireTempCart(cartKey);
        }
        // 返回购物车列表
        return Result.ok(cartItemList);
    }

    /**
     * 修改购物车中商品数量
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public Result updateCartItemNum(@PathVariable Long skuId,
                                    @PathVariable Integer skuNum) {
        cartBizService.updateCartItemNum(skuId, skuNum);
        return Result.ok();
    }

    /**
     * 修改选中状态
     *
     * @param skuId
     * @param checkStatus
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{checkStatus}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer checkStatus) {
        cartBizService.checkCartItem(skuId, checkStatus);
        return Result.ok();
    }


    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable Long skuId) {
        cartBizService.deleteCartItem(skuId);
        return Result.ok();
    }


}
