package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.web.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inner/product")
public class CategoryRpcController {

    @Autowired
    private CategoryBizService categoryBizService;

    /**
     * 数据库插叙所有分类, 封装程一个嵌套的树形结构
     *
     * @return
     */
    @GetMapping("/categorys/tree")
    public Result<List<CategoryVo>> getCategoryTree() {
        List<CategoryVo> tree = categoryBizService.getCategoryTree();
        return Result.ok(tree);
    }

}
