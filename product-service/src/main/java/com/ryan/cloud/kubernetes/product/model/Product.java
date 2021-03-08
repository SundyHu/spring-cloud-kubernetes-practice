package com.ryan.cloud.kubernetes.product.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Product
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-08 15:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_product")
public class Product extends Model<Product> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private BigDecimal price;

    private Integer quantity;
}
