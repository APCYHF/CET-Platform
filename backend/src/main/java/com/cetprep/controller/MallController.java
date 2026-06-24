package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.service.MallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商城接口
 */
@RestController
@RequestMapping("/mall")
@Tag(name = "商城接口")
public class MallController {

    @Autowired
    private MallService mallService;

    @GetMapping("/products")
    @Operation(summary = "商品列表")
    public Result<List> getProducts() {
        return Result.success(mallService.getProducts());
    }

    @PostMapping("/order")
    @Operation(summary = "创建订单")
    public Result<Void> createOrder(@RequestParam Long productId, @RequestParam String payType) {
        mallService.createOrder(productId, payType);
        return Result.success();
    }

    @GetMapping("/orders")
    @Operation(summary = "我的订单")
    public Result<List> getMyOrders() {
        return Result.success(mallService.getMyOrders());
    }
}
