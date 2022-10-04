package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.product.dto.ValueSkuJsonDTO;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 根据spuId查询销售属性的名和值
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrAndValue(Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrAndValue(spuId);
    }

    /**
     * 查询指定sku对应的spu定义的所有属性名和值,并且标记当前sku属性
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrAndValueAndMarkSkuZH(Long spuId, Long skuId) {
        return spuSaleAttrMapper.getSpuSaleAttrAndValueAndMarkSkuZH(spuId, skuId);
    }

    /**
     * 根据spuId查询该spu下所有sku涉及到的所有销售属性值组合
     *
     * @param spuId
     * @return
     */
    @Override
    public String getSpuValuesSkuJson(Long spuId) {
        // 查询
        List<ValueSkuJsonDTO> valueSkuJsonDTOS = spuSaleAttrMapper.getSpuValuesSkuJson(spuId);
        // 封装到map中
        Map<String, Long> map = new HashMap<>();
        valueSkuJsonDTOS.stream().forEach(item -> map.put(item.getAttrValueConcat(), item.getSkuId()));
        // map转为json
        return Jsons.toString(map);
    }
}




