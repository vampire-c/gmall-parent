package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * spu销售属性值
 * @TableName spu_sale_attr_value
 */
@TableName(value ="spu_sale_attr_value")
@Data
public class SpuSaleAttrValue implements Serializable {
    /**
     * 销售属性值编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long spuId;

    /**
     * 销售属性id
     */
    private Long baseSaleAttrId;

    /**
     * 销售属性值名称
     */
    private String saleAttrValueName;

    /**
     * 销售属性名称(冗余)
     */
    private String saleAttrName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}