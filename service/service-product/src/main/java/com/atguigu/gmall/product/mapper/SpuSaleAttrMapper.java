package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.dto.ValueSkuJsonDTO;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Anonymous
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2022-09-26 19:16:22
* @Entity com.atguigu.gmall.product.entity.SpuSaleAttr
*/
@Repository
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId查询销售属性的名和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrAndValue(@Param("spuId") Long spuId);

    /**
     * 查询指定sku对应的spu定义的所有属性名和值,并且标记当前sku属性
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrAndValueAndMarkSkuZH(@Param("spuId") Long spuId, @Param("skuId") Long skuId);

    /**
     * 根据spuId查询该spu下所有sku涉及到的所有销售属性值组合
     * @param spuId
     * @return
     */
    List<ValueSkuJsonDTO> getSpuValuesSkuJson(@Param("spuId") Long spuId);
}




