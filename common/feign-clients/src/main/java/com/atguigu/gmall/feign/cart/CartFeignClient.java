package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/inner/cart")
@FeignClient("service-cart")
public interface CartFeignClient {


    /**
     * 商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Result<AddCartSuccessVo> addToCart(
            @PathVariable Long skuId,
            @RequestParam("skuNum") Integer skuNum);


    /**
     * 删除购物车选中的商品
     *
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked() ;
}
