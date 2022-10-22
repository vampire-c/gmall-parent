package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.web.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/inner/item")
@RestController
public class SkuDetailController {

    @Autowired
    private SkuDetailService skuDetailService;

    /**
     * 返回skuDetail信息
     * 查询商品信息, 并返回指定类型数据
     *
     * @param skuId
     * @return
     */
    @GetMapping("/detail/{skuId}")
    public Result<SkuDetailVo> getSkuDetail(@PathVariable Long skuId) {
        SkuDetailVo skuDetailVo = skuDetailService.getSkuDetail(skuId);
        // 增加商品热度分
        skuDetailService.incrHotScore(skuId);
        return Result.ok(skuDetailVo);
    }
}
