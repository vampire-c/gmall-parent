package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.biz.SearchBizService;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/inner/search")
@RestController
public class SearchController {

    @Autowired
    private SearchBizService searchBizService;


    /**
     * 检索服务
     *
     * @param searchParamVo
     * @return
     */
    @ApiOperation("商品检索")
    @PostMapping("/skuInfo/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo) {
        SearchResponseVo searchResponseVo = searchBizService.search(searchParamVo);
        return Result.ok(searchResponseVo);
    }

    /**
     * 商品上架保存到es
     *
     * @param goods
     * @return
     */
    @PostMapping("/onSaleGoods")
    public Result onSaleGoods(@RequestBody Goods goods) {
        searchBizService.onSaleGoods(goods);
        return Result.ok();
    }

    /**
     * 商品下架,数据从es中删除
     *
     * @param skuId
     */
    @DeleteMapping("/cancelSaleGoods/{skuId}")
    public Result cancelSaleGoods(@PathVariable Long skuId) {
        searchBizService.cancelSaleGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新热度分
     *
     * @param skuId
     * @param hotScore
     * @return
     */
    @GetMapping("/hotscore/{skuId}")
    public Result updateHotScore(@PathVariable Long skuId,
                                 @RequestParam("hotScore") Long hotScore) {
        searchBizService.updateHotScore(skuId, hotScore);
        return Result.ok();
    }


}
