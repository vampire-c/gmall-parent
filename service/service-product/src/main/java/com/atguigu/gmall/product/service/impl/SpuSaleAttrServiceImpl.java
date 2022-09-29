package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
        implements SpuSaleAttrService {


    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 查询销售属性的名和值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }
}




