package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;


@FeignClient("service-product")
@RequestMapping("/api/inner/product")
public interface SkuDetailFeignClient {

    /**
     * sku_info信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable Long skuId);


    /**
     * skuImageList
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/images/{skuId}")
    public Result<List<SkuImage>> getSkuImages(@PathVariable Long skuId);


    /**
     * 根据skuInfo的三级分类id查询完整三层路径信息
     *
     * @param c3Id
     * @return
     */
    @GetMapping("/skuInfo/categoryView/{c3Id}")
    public Result<SkuDetailVo.CategoryView> getCategoryView(@PathVariable Long c3Id);


    /**
     * 查询SkuInfo的价格
     *
     * @return
     */
    @GetMapping("/skuInfo/price/{skuId}")
    public Result<BigDecimal> getSkuInfoPrice(@PathVariable Long skuId);


    /**
     * 查询指定sku对应的spu定义的所有属性名和值,并且标记当前sku属性
     *
     * @return
     */
    @GetMapping("/skuInfo/spuSaleAttrAndValue/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrAndValueAndMarkSkuZH(@PathVariable Long spuId, @PathVariable Long skuId);


    /**
     * 根据spuId查询该spu下所有sku涉及到的所有销售属性值组合
     *
     * @param spuId
     * @return
     */
    @GetMapping("/skuInfo/ValuesSkuJson/{spuId}")
    public Result<String> getSpuValuesSkuJson(@PathVariable Long spuId);
}
