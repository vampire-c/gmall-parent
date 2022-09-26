package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 属性值表
 * @TableName base_attr_value
 */
@TableName(value ="base_attr_value")
@Data
public class BaseAttrValue implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属性值名称
     */
    private String valueName;

    /**
     * 属性id
     */
    private Long attrId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}