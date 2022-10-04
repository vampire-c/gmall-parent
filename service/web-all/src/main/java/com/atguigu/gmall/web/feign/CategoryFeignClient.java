package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.web.CategoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("service-product")
@RequestMapping("/api/inner/product")
public interface CategoryFeignClient {

    @GetMapping("/categorys/tree")
    Result<List<CategoryVo>> getCategoryTree();
}
