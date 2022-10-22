package com.atguigu.gmall.search.vo;

import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponseVo {

    //
    private SearchParamVo searchParam;

    // 品牌面包屑
    private String trademarkParam;

    // 属性面包屑
    private List<SearchAttr> propsParamList;

    // 品牌列表
    private List<SearchTmVo> trademarkList;

    // 属性列表
    private List<SearchRespAttr> attrsList;

    // 排序信息
    private SearchOrderMapVo orderMap;

    // 商品信息
    private List<Goods> goodsList;

    // 当前页码
    private Integer pageNo;

    // 总页码
    private Long totalPages;

    // url参数
    private String urlParam;


}
