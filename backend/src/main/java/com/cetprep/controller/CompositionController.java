package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.entity.CompositionTranslation;
import com.cetprep.service.CompositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作文/翻译接口
 */
@RestController
@RequestMapping("/composition")
@Tag(name = "作文翻译接口")
public class CompositionController {

    @Autowired
    private CompositionService compositionService;

    @PostMapping("/submit")
    @Operation(summary = "提交作文/翻译")
    public Result<Void> submit(@RequestBody CompositionTranslation ct) {
        compositionService.submit(ct);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的提交记录")
    public Result<List> getMyList() {
        return Result.success(compositionService.getMyList());
    }
}
