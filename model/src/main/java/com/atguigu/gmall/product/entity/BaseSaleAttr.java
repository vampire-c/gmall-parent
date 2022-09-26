package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 基本销售属性表
 * @TableName base_sale_attr
 */
@TableName(value ="base_sale_attr")
@Data
public class BaseSaleAttr implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 销售属性名称
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}