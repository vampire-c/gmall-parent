package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_category2(二级分类表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface BaseCategory2Service extends IService<BaseCategory2> {

    /**
     * 根据一级分类id,查询二级分类
     *
     * @param category_id
     * @return
     */
    List<BaseCategory2> getCategory2(Long category_id);
}
