package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 购物车
 */
@Slf4j
@Controller
public class CartController {

    @Autowired
    private CartFeignClient cartFeignClient;
    // 1 将当前线程作为key, 当前请求作为value存在map中
    // public static final Map<Thread,HttpServletRequest> requestMap = new ConcurrentHashMap<>();
    // 2 同一个线程共享数据
    // public static final ThreadLocal<HttpServletRequest> threadLocal = new ThreadLocal<>();
    // 3 spring会自动把当前正在处理的请求共享到当前线程


    /**
     * 把商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          // @RequestHeader(value = RedisConst.USER_ID_HEADER, required = false) Long userInfoId,
                          // @RequestHeader(value = RedisConst.USER_TEMP_ID_HEADER, required = false) String userTempId,
                          // HttpServletRequest request,
                          Model model) {
        // requestMap.put(Thread.currentThread(),request);
        // threadLocal.set(request);
        Result<AddCartSuccessVo> result = cartFeignClient.addToCart(skuId, skuNum);
        if (result.isOk()) {
            model.addAttribute("skuInfo", result.getData().getSkuInfo());
            model.addAttribute("skuNum", result.getData().getSkuNum());
            return "cart/addCart";
        } else {
            log.info("加入购物车失败,{}", result.getData());
            model.addAttribute("message", result.getMessage());
            return "cart/error";
        }
        // requestMap.remove(Thread.currentThread());
        // threadLocal.remove();
    }


    /**
     * 跳转到购物车页面
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartList() {
        return "cart/index";
    }

    /**
     * 删除选中的商品
     *
     * @return
     */
    @GetMapping("/cart/deleteChecked")
    public String deleteChecked() {
        /*
        redirect: 重定向
        forward: 请求转发
         */
        // 远程调用删除选中的商品
        cartFeignClient.deleteChecked();
        // 重定向到购物车列表页
        return "redirect:http://cart.gmall.com/cart.html";
        // return "cart/index";
    }


}
