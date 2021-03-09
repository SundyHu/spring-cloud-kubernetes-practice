package com.ryan.cloud.kubernetes.order.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Order
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-09 14:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String orderCode;

    private Integer userId;

    private Integer payMethod;

}
