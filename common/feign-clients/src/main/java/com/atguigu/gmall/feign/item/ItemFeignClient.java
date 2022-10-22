package com.atguigu.gmall.feign.item;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.web.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/inner/item")
@FeignClient("service-item")
public interface ItemFeignClient {

    /**
     * 获取商品详情数据
     * @param skuId
     * @return
     */
    @GetMapping("/detail/{skuId}")
    Result<SkuDetailVo> getSkuDetail(@PathVariable Long skuId);
}
