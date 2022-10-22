package com.atguigu.gmall.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户地址表
 * @TableName user_address
 */
@TableName(value ="user_address")
@Data
public class UserAddress implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 收件人
     */
    private String consignee;

    /**
     * 联系方式
     */
    private String phoneNum;

    /**
     * 是否是默认
     */
    private String isDefault;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}