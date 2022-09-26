package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 属性表
 * @TableName base_attr_info
 */
@TableName(value ="base_attr_info")
@Data
public class BaseAttrInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类层级
     */
    private Integer categoryLevel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}