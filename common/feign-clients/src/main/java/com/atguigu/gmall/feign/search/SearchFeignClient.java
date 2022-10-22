package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/inner/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    /**
     * 检索服务
     *
     * @param searchParamVo
     * @return
     */
    @PostMapping("/skuInfo/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo);

    /**
     * 商品上架保存到es
     *
     * @param goods
     * @return
     */
    @PostMapping("/onSaleGoods")
    public Result onSaleGoods(@RequestBody Goods goods);


    /**
     * 商品下架,数据从es中删除
     *
     * @param skuId
     */
    @DeleteMapping("/cancelSaleGoods/{skuId}")
    public Result cancelSaleGoods(@PathVariable Long skuId);

    /**
     * 更新热度分
     *
     * @param skuId
     * @param hotScore
     * @return
     */
    @GetMapping("/hotscore/{skuId}")
    public Result updateHotScore(@PathVariable Long skuId,
                                 @RequestParam("hotScore") Long hotScore);
}
