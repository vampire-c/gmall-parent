package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.vo.SpuInfoSaveVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Anonymous
 * @description 针对表【spu_info(商品表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface SpuInfoService extends IService<SpuInfo> {


    /**
     * 分页查询SPU列表
     *
     * @param pageNum
     * @param limitNum
     * @param category3Id
     * @return
     */
    Page<SpuInfo> getSpuInfoPage(Long pageNum, Long limitNum, Long category3Id);


    /**
     * 添加保存SPU信息
     *
     * @param spuInfoSaveVo
     */
    void saveSpuInfo(SpuInfoSaveVo spuInfoSaveVo);
}
