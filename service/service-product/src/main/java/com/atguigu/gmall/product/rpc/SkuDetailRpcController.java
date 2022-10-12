package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.entity.CategoryViewEntity;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.web.SkuDetailVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 查询商品信息远程接口
 */
@RestController
@RequestMapping("/api/inner/product")
public class SkuDetailRpcController {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImageService skuImageService;

    @Autowired
    private CategoryBizService categoryBizService;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService;


    /**
     * sku_info信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable Long skuId) {
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return Result.ok(skuInfo);
    }


    /**
     * skuImageList
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/images/{skuId}")
    public Result<List<SkuImage>> getSkuImages(@PathVariable Long skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId);
        List<SkuImage> list = skuImageService.list(queryWrapper);
        return Result.ok(list);
    }


    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     *
     * @param c3Id
     * @return
     */
    @GetMapping("/skuInfo/categoryView/{c3Id}")
    public Result<SkuDetailVo.CategoryView> getCategoryView(@PathVariable Long c3Id) {
        // 查询
        CategoryViewEntity categoryViewEntity = categoryBizService.getCategoryView(c3Id);

        // 封装为所需类型
        SkuDetailVo.CategoryView categoryView = new SkuDetailVo.CategoryView();
        categoryView.setCategory1Id(categoryViewEntity.getC1id());
        categoryView.setCategory1Name(categoryViewEntity.getC1name());
        categoryView.setCategory2Id(categoryViewEntity.getC2id());
        categoryView.setCategory2Name(categoryViewEntity.getC2name());
        categoryView.setCategory3Id(categoryViewEntity.getC3id());
        categoryView.setCategory3Name(categoryViewEntity.getC3name());

        return Result.ok(categoryView);
    }

    /**
     * 查询SkuInfo的价格
     *
     * @return
     */
    @GetMapping("/skuInfo/price/{skuId}")
    public Result<BigDecimal> getSkuInfoPrice(@PathVariable Long skuId) {
        BigDecimal price = skuInfoService.getSkuInfoPrice(skuId);
        return Result.ok(price);
    }

    /**
     * 查询指定sku对应的spu定义的所有属性名和值,并且标记当前sku属性
     *
     * @return
     */
    @GetMapping("/skuInfo/spuSaleAttrAndValue/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrAndValueAndMarkSkuZH(@PathVariable Long spuId, @PathVariable Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrAndValueAndMarkSkuZH(spuId, skuId);
        return Result.ok(spuSaleAttrList);
    }


    /**
     * 根据spuId查询该spu下所有sku涉及到的所有销售属性值组合
     *
     * @param spuId
     * @return
     */
    @GetMapping("/skuInfo/ValuesSkuJson/{spuId}")
    public Result<String> getSpuValuesSkuJson(@PathVariable Long spuId) {
        String json = spuSaleAttrService.getSpuValuesSkuJson(spuId);
        return Result.ok(json);
    }


    /**
     * 获取所有skuId
     *
     * @return
     */
    @GetMapping("/skuInfo/skuIds")
    public Result<List<Long>> getAllSkuIds() {
        List<Long> ids = skuInfoService.getAllSkuIds();
        return Result.ok(ids);
    }

}
