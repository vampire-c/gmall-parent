package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
        implements SkuAttrValueService {

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    /**
     * 查询某商品的所有平台属性名和值
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SearchAttr> getSkuAttrAndValue(Long skuId) {
        return skuAttrValueMapper.getSkuAttrAndValue(skuId);
    }
}




