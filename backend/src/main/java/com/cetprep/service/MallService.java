package com.cetprep.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cetprep.entity.MallOrder;
import com.cetprep.entity.MallProduct;
import com.cetprep.entity.User;
import com.cetprep.exception.BusinessException;
import com.cetprep.mapper.MallOrderMapper;
import com.cetprep.mapper.MallProductMapper;
import com.cetprep.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商城服务
 */
@Service
public class MallService {

    @Autowired
    private MallProductMapper productMapper;
    @Autowired
    private MallOrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    public List<MallProduct> getProducts() {
        return productMapper.selectList(new QueryWrapper<MallProduct>().eq("status", 1));
    }

    @Transactional
    public void createOrder(Long productId, String payType) {
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        MallProduct product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }

        MallOrder order = new MallOrder();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setType("PRODUCT");
        order.setProductId(productId);
        order.setAmount(product.getPrice());
        order.setStatus("UNPAID");
        order.setPayType(payType);

        if ("POINTS".equals(payType)) {
            if (user.getPoints() < product.getPoints()) {
                throw new BusinessException("积分不足");
            }
            user.setPoints(user.getPoints() - product.getPoints());
            userMapper.updateById(user);
            order.setPointsUsed(product.getPoints());
            order.setStatus("PAID");
            order.setPayTime(LocalDateTime.now());
        }

        orderMapper.insert(order);
    }

    public List<MallOrder> getMyOrders() {
        return orderMapper.selectList(
                new QueryWrapper<MallOrder>()
                        .eq("user_id", getCurrentUserId())
                        .orderByDesc("created_at"));
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null ? user.getId() : null;
    }
}
