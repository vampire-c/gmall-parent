package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseSaleAttr;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "SPU销售属性接口")
@RequestMapping("/admin/product")
@RestController
public class BaseSaleAttrController {

    @Autowired
    private BaseSaleAttrService baseSaleAttrService;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

    /**
     * 获取销售属性列表
     *
     * @return
     */
    @ApiOperation("获取销售属性列表")
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrService.list();
        return Result.ok(baseSaleAttrList);
    }

    /**
     * 根据spuId查询销售属性的名和值
     *
     * @param spuId
     * @return
     */
    @ApiOperation("查询销售属性的名和值")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrAndValue(spuId);
        return Result.ok(spuSaleAttrList);
    }

}
