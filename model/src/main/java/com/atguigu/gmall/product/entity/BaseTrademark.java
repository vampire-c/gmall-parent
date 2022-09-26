package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 品牌表
 * @TableName base_trademark
 */
@TableName(value ="base_trademark")
@Data
public class BaseTrademark implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属性值
     */
    private String tmName;

    /**
     * 品牌logo的图片路径
     */
    private String logoUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}