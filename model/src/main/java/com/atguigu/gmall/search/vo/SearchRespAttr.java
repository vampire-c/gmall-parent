package com.atguigu.gmall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchRespAttr {

    // 属性id
    private Long attrId;

    // 属性名
    private String attrName;

    // 属性值集合
    private List<String> attrValueList;

}
