package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.web.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Anonymous
* @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
* @createDate 2022-09-26 19:16:22
* @Entity com.atguigu.gmall.product.entity.BaseCategory1
*/
@Repository
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {

    /**
     * 数据库插叙所有分类, 封装程一个嵌套的树形结构
     * @return
     */
    List<CategoryVo> getCategoryTree();


    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     * @param c3Id
     * @return
     */
    CategoryViewEntity getCategoryView(@Param("c3Id") Long c3Id);

}




