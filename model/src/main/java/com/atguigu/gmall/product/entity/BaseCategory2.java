package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 二级分类表
 * @TableName base_category2
 */
@TableName(value ="base_category2")
@Data
public class BaseCategory2 implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 二级分类名称
     */
    private String name;

    /**
     * 一级分类编号
     */
    private Long category1Id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}