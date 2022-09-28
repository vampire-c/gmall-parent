package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Anonymous
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service
* @createDate 2022-09-26 19:16:22
*/
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    /**
     * 根据平台属性ID获取平台属性
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);
}
