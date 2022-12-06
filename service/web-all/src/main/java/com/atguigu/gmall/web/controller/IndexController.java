package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.web.CategoryVo;
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

        // options精确设置Feign调用超时时间,如果超时就中断连接,不重试
        // Request.Options options = new Request.Options(1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS, false);
        // Result<List<CategoryVo>> categoryTree = categoryFeignClient.getCategoryTree(options);

        Result<List<CategoryVo>> categoryTree = categoryFeignClient.getCategoryTree();
        List<CategoryVo> list = categoryTree.getData();
        model.addAttribute("list", list);
        return "index/index";
    }
}
