package com.atguigu.gmall.search.vo;

import lombok.Data;

@Data
public class SearchParamVo {
    // 分类信息
    private Long category1Id;
    private Long category2Id;
    private Long category3Id;

    // 关键字
    private String keyword;

    // 品牌条件
    private String trademark;

    // 属性条件
    private String[] props;

    // 排序条件
    private String order;

    // 页码信息
    private Integer pageNo = 1;

    // 页码长度
    private Integer pageSize = 10;


}
