package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@TableName("mall_order")
public class MallOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private String type; // PRODUCT, CORRECTION, COURSE
    private Long productId;
    private Long compositionId;
    private BigDecimal amount;
    private Integer pointsUsed;
    private String payType; // WECHAT, ALIPAY, POINTS
    private String status; // UNPAID, PAID, SHIPPED, COMPLETED, CANCELLED
    private LocalDateTime payTime;
    private LocalDateTime createdAt;
}
