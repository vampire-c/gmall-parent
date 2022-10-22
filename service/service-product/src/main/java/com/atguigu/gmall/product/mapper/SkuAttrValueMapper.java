package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
 * @createDate 2022-09-26 19:16:22
 * @Entity com.atguigu.gmall.product.entity.SkuAttrValue
 */
@Repository
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    /**
     * 查询某商品的所有平台属性名和值
     *
     * @param skuId
     * @return
     */
    List<SearchAttr> getSkuAttrAndValue(@Param("skuId") Long skuId);
}




