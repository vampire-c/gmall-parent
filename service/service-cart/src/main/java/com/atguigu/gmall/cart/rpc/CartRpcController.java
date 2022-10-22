package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartBizService;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/inner/cart")
@RestController
public class CartRpcController {

    @Autowired
    private CartBizService cartBizService;

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
            @RequestParam("skuNum") Integer skuNum
            // , @RequestHeader(value = RedisConst.USER_ID_HEADER, required = false) Long userInfoId,
            // @RequestHeader(value = RedisConst.USER_TEMP_ID_HEADER, required = false) String userTempId
    ) {

        // log.info("添加 {} 商品到购物车", skuId);
        AddCartSuccessVo addCartSuccessVo = cartBizService.addToCart(skuId, skuNum);

        return Result.ok(addCartSuccessVo);
    }

    /**
     * 删除购物车选中的商品
     *
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked() {
        cartBizService.deleteChecked();
        return Result.ok();
    }


}
