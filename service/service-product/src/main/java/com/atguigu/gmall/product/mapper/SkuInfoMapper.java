package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
 * @createDate 2022-09-26 19:16:22
 * @Entity com.atguigu.gmall.product.entity.SkuInfo
 */
@Repository
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 修改上下架状态
     *
     * @param skuId
     * @param status
     */
    void updateSaleStatus(@Param("skuId") Long skuId, @Param("status") int status);

    /**
     * 查询SkuInfo的价格
     *
     * @param skuId
     * @return
     */
    BigDecimal getSkuInfoPrice(@Param("skuId") Long skuId);

    /**
     * 获取所有skuId
     *
     * @return
     */
    List<Long> getAllSkuIds();

}




