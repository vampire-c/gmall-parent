package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Anonymous
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2022-09-26 19:16:22
*/
public interface SpuImageService extends IService<SpuImage> {

    /**
     * 查询SPU的图片列表
     * @param spuId
     */
    List<SpuImage> getSpuImageList(Long spuId);
}
