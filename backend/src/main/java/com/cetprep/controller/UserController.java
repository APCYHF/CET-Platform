package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.service.UserService;
import com.cetprep.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人中心接口
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserVO> getProfile() {
        return Result.success(userService.getCurrentUser());
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public Result<Void> updateProfile(@RequestBody UserVO vo) {
        userService.updateUser(vo);
        return Result.success();
    }
}
