package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.dto.LoginDTO;
import com.cetprep.dto.RegisterDTO;
import com.cetprep.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口（登录/注册）
 */
@RestController
@Slf4j
@RequestMapping("/auth")
@Tag(name = "认证接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        log.info("用户登录: username={}", dto.getUsername());
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        userService.register(dto);
        log.info("用户注册: username={}", dto.getUsername());
        return Result.success();
    }
}
