package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.entity.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "平台属性列表接口")
@RequestMapping("/admin/product")
@RestController
public class BaseCategoryController {

    @Autowired
    private BaseCategory1Service baseCategory1Service;

    @Autowired
    private BaseCategory2Service baseCategory2Service;

    @Autowired
    private BaseCategory3Service baseCategory3Service;

    /**
     * 查询一级分类
     * @return
     */
    @ApiOperation("查询一级分类")
    @GetMapping("/getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> list = this.baseCategory1Service.list();
        return Result.ok(list);
    }


    /**
     * 根据一级分类id,查询二级分类
     *
     * @param category_id
     * @return
     */
    @ApiOperation("根据一级分类id,查询二级分类")
    @GetMapping("/getCategory2/{category_id}")
    public Result getCategory2(@PathVariable Long category_id) {
        List<BaseCategory2> list = this.baseCategory2Service.getCategory2(category_id);
        return Result.ok(list);
    }

    /**
     * 根据二级分类id,查询三级分类
     *
     * @param category_id
     * @return
     */
    @ApiOperation("根据二级分类id,查询三级分类")
    @GetMapping("/getCategory3/{category_id}")
    public Result getCategory3(@PathVariable Long category_id) {
        List<BaseCategory3> list = this.baseCategory3Service.getCategory3(category_id);
        return Result.ok(list);
    }
}
