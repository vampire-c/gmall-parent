package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "品牌列表接口")
@RequestMapping("/admin/product")
@RestController
public class TrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 分页查询品牌列表
     * /baseTrademark/1/10
     *
     * @param pageNum
     * @param limitNum
     * @return
     */
    @ApiOperation("查询品牌分页列表")
    @GetMapping("/baseTrademark/{pageNum}/{limitNum}")
    public Result baseTrademark(@PathVariable Long pageNum, @PathVariable Long limitNum) {
        Page<BaseTrademark> page = new Page<>(pageNum, limitNum);
        Page<BaseTrademark> result = this.baseTrademarkService.page(page);
        return Result.ok(result);
    }


    /**
     * 添加品牌
     *
     * @param baseTrademark
     * @return
     */
    @ApiOperation("添加品牌")
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        this.baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 删除品牌
     *
     * @param id
     * @return
     */
    @ApiOperation("删除品牌")
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result BaseTrademark(@PathVariable Long id) {
        this.baseTrademarkService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改品牌
     *
     * @param baseTrademark
     * @return
     */
    @ApiOperation("修改品牌")
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        this.baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询品牌")
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable Long id) {
        BaseTrademark baseTrademark = this.baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }
}
