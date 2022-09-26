package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseCategory3;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
        implements BaseCategory3Service {

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    /**
     * 根据二级分类id,查询三级分类
     *
     * @param category_id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category_id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id", category_id);
        List<BaseCategory3> list = baseCategory3Mapper.selectList(queryWrapper);
        return list;
    }
}




