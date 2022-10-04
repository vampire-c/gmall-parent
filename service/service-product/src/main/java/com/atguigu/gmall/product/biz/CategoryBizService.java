package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.web.CategoryVo;

import java.util.List;

public interface CategoryBizService {
    /**
     * 数据库插叙所有分类, 封装程一个嵌套的树形结构
     *
     * @return
     */
    List<CategoryVo> getCategoryTree();

    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     *
     * @param c3Id
     * @return
     */
    CategoryViewEntity getCategoryView(Long c3Id);
}
