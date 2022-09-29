package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.vo.SpuInfoSaveVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "商品属性SPU管理接口")
@RequestMapping("/admin/product")
@RestController
public class SpuController {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private SpuImageService spuImageService;

    /**
     * 分页查询SPU列表
     */
    @ApiOperation("分页查询SPU列表")
    @GetMapping("/{pageNum}/{limitNum}")
    public Result getSpuInfoPage(@PathVariable Long pageNum,
                                 @PathVariable Long limitNum,
                                 @RequestParam("category3Id") Long category3Id) {
        Page<SpuInfo> spuInfoPage = spuInfoService.getSpuInfoPage(pageNum, limitNum, category3Id);
        return Result.ok(spuInfoPage);
    }

    /**
     * 添加保存SPU信息
     *
     * @param spuInfoSaveVo
     * @return
     */
    @ApiOperation("添加保存SPU信息")
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfoSaveVo spuInfoSaveVo) {

        spuInfoService.saveSpuInfo(spuInfoSaveVo);
        return Result.ok();
    }


    /**
     * 查询SPU的图片列表
     * @param spuId
     * @return
     */
    @ApiOperation("查询SPU的图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId) {
        List<SpuImage> spuImageList = spuImageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }




}
