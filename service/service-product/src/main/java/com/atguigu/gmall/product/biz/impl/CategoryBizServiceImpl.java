package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.web.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryBizServiceImpl implements CategoryBizService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    /**
     * 数据库插叙所有分类, 封装程一个嵌套的树形结构
     *
     * @return
     */
    @Override
    public List<CategoryVo> getCategoryTree() {
        List<CategoryVo> tree = baseCategory1Mapper.getCategoryTree();
        return tree;
    }


    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     *
     * @param c3Id
     * @return
     */
    @Override
    public CategoryViewEntity getCategoryView(Long c3Id) {
        return baseCategory1Mapper.getCategoryView(c3Id);
    }

}
