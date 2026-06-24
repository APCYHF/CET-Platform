package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商城商品
 */
@Data
@TableName("mall_product")
public class MallProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type; // PHYSICAL, DIGITAL, COURSE
    private String description;
    private String cover;
    private BigDecimal price;
    private Integer points;
    private Integer stock;
    private Integer status;
    private LocalDateTime createdAt;
}
