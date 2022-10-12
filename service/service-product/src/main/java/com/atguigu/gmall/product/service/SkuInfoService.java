package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.vo.SkuInfoSaveVo;
import com.atguigu.gmall.product.vo.SkuInfoUpdateVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 分页查询SKU列表
     *
     * @param pageNum
     * @param limitNum
     * @return
     */
    Page<SkuInfo> getSpuInfoPage(Long pageNum, Long limitNum);

    /**
     * 添加保存sku
     *
     * @param skuInfoSaveVo
     */
    void skuInfoService(SkuInfoSaveVo skuInfoSaveVo);

    /**
     * 修改上下架状态
     *
     * @param skuId
     * @param status
     */
    void changeOnSale(Long skuId, int status);


    /**
     * 查询SkuInfo的价格
     *
     * @param skuId
     * @return
     */
    BigDecimal getSkuInfoPrice(Long skuId);

    /**
     * 获取所有skuId
     *
     * @return
     */
    List<Long> getAllSkuIds();


    // 修改skuInfo
    void updateSkuInfo(SkuInfoUpdateVo skuInfoUpdateVo);
}
