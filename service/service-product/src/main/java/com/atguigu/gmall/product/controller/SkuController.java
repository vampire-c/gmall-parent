package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.vo.SkuInfoSaveVo;
import com.atguigu.gmall.product.vo.SkuInfoUpdateVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "商品属性SKU管理接口")
@RequestMapping("/admin/product")
@RestController
public class SkuController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 分页查询SKU列表
     *
     * @param pageNum
     * @param limitNum
     * @return
     */
    @ApiOperation("分页查询SKU列表")
    @GetMapping("/list/{pageNum}/{limitNum}")
    public Result getSkuInfoPage(@PathVariable Long pageNum,
                                 @PathVariable Long limitNum) {
        Page<SkuInfo> skuInfoPage = skuInfoService.getSpuInfoPage(pageNum, limitNum);
        return Result.ok(skuInfoPage);
    }

    /**
     * 添加保存sku
     *
     * @param skuInfoSaveVo
     * @return
     */
    @ApiOperation("添加保存sku")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfoSaveVo skuInfoSaveVo) {
        skuInfoService.skuInfoService(skuInfoSaveVo);
        // log.info("sku: {}" ,skuInfoSaveVo);
        return Result.ok();
    }

    /**
     * 上架
     *
     * @param skuId
     * @return
     */
    @ApiOperation("上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId) {
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 下架
     *
     * @param skuId
     * @return
     */
    @ApiOperation("下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId) {

        skuInfoService.cancelSale(skuId);

        return Result.ok();
    }

    // 伪 修改skuInfo
    @PutMapping("/updateSkuInfo")
    public Result updateSkuInfo(SkuInfoUpdateVo skuInfoUpdateVo){
        skuInfoService.updateSkuInfo(skuInfoUpdateVo);
        return Result.ok();
    }



}
