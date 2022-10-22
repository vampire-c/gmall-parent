package com.atguigu.gmall.web.controller;


import com.atguigu.gmall.web.SkuDetailVo;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    /**
     * 商品详情页
     *
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId, Model model) {
        SkuDetailVo skuDetailVo = null;
        // 远程调用 service-item 获取商品详情数据
        skuDetailVo = itemFeignClient.getSkuDetail(skuId).getData();

        if (StringUtils.isEmpty(skuDetailVo)) {
            return "item/error";

        }

        // categoryView {category1Id,category1Name,category2Id,category2Name,category3Id,category3Name}
        // 当前sku所属的完整分类信息
        model.addAttribute("categoryView", skuDetailVo.getCategoryView());

        // skuInfo {skuName, skuDefaultImg, skuImageList{imgUrl, } }
        // 当前sku的信息
        model.addAttribute("skuInfo", skuDetailVo.getSkuInfo());

        // price
        // sku的价格
        model.addAttribute("price", skuDetailVo.getPrice());

        // sku的销售属性列表
        model.addAttribute("spuSaleAttrList", skuDetailVo.getSpuSaleAttrList());

        // sku的值json
        model.addAttribute("valuesSkuJson", skuDetailVo.getValuesSkuJson());

        return "item/index";
    }
}
