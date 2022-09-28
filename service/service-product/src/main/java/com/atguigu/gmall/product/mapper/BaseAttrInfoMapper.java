package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Anonymous
 * @description 针对表【base_attr_info(属性表)】的数据库操作Mapper
 * @createDate 2022-09-26 19:16:22
 * @Entity com.atguigu.gmall.product.entity.BaseAttrInfo
 */
@Repository
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据分类id获取属性值列表
     *
     * @param c1id
     * @param c2id
     * @param c3id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(@Param("c1id") Long c1id, @Param("c2id") Long c2id, @Param("c3id") Long c3id);

}




