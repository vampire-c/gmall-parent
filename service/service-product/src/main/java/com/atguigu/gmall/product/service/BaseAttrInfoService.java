package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service
 * @createDate 2022-09-26 19:16:22
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    /**
     * 根据分类id获取属性值列表
     *
     * @param c1id
     * @param c2id
     * @param c3id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(Long c1id, Long c2id, Long c3id);

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 修改平台属性
     * @param baseAttrInfo
     */
    void updateAttrInfo(BaseAttrInfo baseAttrInfo);

}
