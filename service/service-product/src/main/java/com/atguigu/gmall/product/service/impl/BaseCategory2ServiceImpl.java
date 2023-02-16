package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
        implements BaseCategory2Service {


    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 根据一级分类id,查询二级分类
     *
     * @param category_id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category_id) {
        // QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("category1_id", category_id);

        LambdaQueryWrapper<BaseCategory2> queryWrapper = new LambdaQueryWrapper<BaseCategory2>().eq(BaseCategory2::getCategory1Id, category_id);
        List<BaseCategory2> list = baseCategory2Mapper.selectList(queryWrapper);
        return list;
    }
}




