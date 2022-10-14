package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.web.CategoryVo;
import com.atguigu.gmall.web.feign.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private CategoryFeignClient categoryFeignClient;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 远程调用 service-product 数据库查询所有分类, 封装程一个嵌套的树形结构
        Result<List<CategoryVo>> categoryTree = categoryFeignClient.getCategoryTree();
        List<CategoryVo> list = categoryTree.getData();
        model.addAttribute("list", list);
        return "index/index";
    }
}
