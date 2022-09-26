package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 三级分类表
 * @TableName base_category3
 */
@TableName(value ="base_category3")
@Data
public class BaseCategory3 implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 三级分类名称
     */
    private String name;

    /**
     * 二级分类编号
     */
    private Long category2Id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}