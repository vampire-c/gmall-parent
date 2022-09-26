package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_category3(三级分类表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface BaseCategory3Service extends IService<BaseCategory3> {

    /**
     * 根据二级分类id,查询三级分类
     *
     * @param category_id
     * @return
     */
    List<BaseCategory3> getCategory3(Long category_id);
}
