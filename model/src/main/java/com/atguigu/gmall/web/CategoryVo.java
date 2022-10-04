package com.atguigu.gmall.web;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {

    private Long categoryId;
    private String categoryName;
    private List<CategoryVo> categoryChild;
}
