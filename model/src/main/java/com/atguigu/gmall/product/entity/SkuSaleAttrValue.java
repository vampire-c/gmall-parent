package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * sku销售属性值
 * @TableName sku_sale_attr_value
 */
@TableName(value ="sku_sale_attr_value")
@Data
public class SkuSaleAttrValue implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存单元id
     */
    private Long skuId;

    /**
     * spu_id(冗余)
     */
    private Integer spuId;

    /**
     * 销售属性值id
     */
    private Long saleAttrValueId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}