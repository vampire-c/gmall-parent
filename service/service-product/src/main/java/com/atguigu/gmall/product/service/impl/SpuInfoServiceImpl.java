package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.entity.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.atguigu.gmall.product.vo.SpuInfoSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anonymous
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-09-26 19:16:22
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuImageService spuImageService;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

    @Autowired
    private SpuSaleAttrValueService spuSaleAttrValueService;


    /**
     * 分页查询SPU列表
     *
     * @param pageNum
     * @param limitNum
     * @param category3Id
     * @return
     */
    @Override
    public Page<SpuInfo> getSpuInfoPage(Long pageNum, Long limitNum, Long category3Id) {
        Page<SpuInfo> page = new Page<>(pageNum, limitNum);
        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id, category3Id);
        return spuInfoMapper.selectPage(page, queryWrapper);
    }

    /**
     * 添加保存SPU信息
     *
     * @param spuInfoSaveVo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoSaveVo spuInfoSaveVo) {
        // 保存SpuInfo中的信息
        SpuInfo spuInfo = new SpuInfo();
        // 将vo中的数据对拷给和数据表匹配的实例
        BeanUtils.copyProperties(spuInfoSaveVo, spuInfo);
        save(spuInfo);// 保存SpuInfo中的信息(商品表)
        Long spuId = spuInfo.getId();

        // 保存spuImageList 图片
        List<SpuImage> spuImageList =
                spuInfoSaveVo.getSpuImageList() // 获取vo中image数据
                        .stream().map(item -> {
                            SpuImage spuImage = new SpuImage();
                            // 将vo中的数据对拷给和数据表匹配的实例
                            BeanUtils.copyProperties(item, spuImage);
                            // vo中没有的数据回填, spuId
                            spuImage.setSpuId(spuId);
                            return spuImage;
                        }).collect(Collectors.toList());
        spuImageService.saveBatch(spuImageList);// 批量存储spuImage 图片数据

        // 保存spuSaleAttrList 销售属性
        spuInfoSaveVo.getSpuSaleAttrList() // 获取vo中的spuSaleAttrList
                .stream().forEach(item -> {
                    // 保存SpuSaleAttr spu销售属性名
                    SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
                    // 将vo中的数据对拷给和数据表匹配的实例
                    BeanUtils.copyProperties(item, spuSaleAttr);
                    // vo中没有的数据回填, spuId
                    spuSaleAttr.setSpuId(spuId);
                    // 保存SpuSaleAttr中的信息(spu销售属性名)
                    spuSaleAttrService.save(spuSaleAttr);

                    // 保存SpuSaleAttrValueList spu销售属性值
                    List<SpuSaleAttrValue> spuSaleAttrValueList =
                            item.getSpuSaleAttrValueList() // 获取spuSaleAttrList中的spuSaleAttrValueList
                                    .stream().map(ite -> {
                                        // 保存SpuSaleAttrValue spu销售属性值
                                        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
                                        // 将vo中的数据对拷给和数据表匹配的实例
                                        BeanUtils.copyProperties(ite, spuSaleAttrValue);
                                        // vo中没有的数据回填, spuId, saleAttrName
                                        spuSaleAttrValue.setSpuId(spuId);
                                        spuSaleAttrValue.setSaleAttrName(item.getSaleAttrName());
                                        return spuSaleAttrValue;
                                    }).collect(Collectors.toList());
                    spuSaleAttrValueService.saveBatch(spuSaleAttrValueList); // 批量存储SpuSaleAttrValue spu销售属性值
                });
    }

}




