package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.entity.CetScore;
import com.cetprep.service.CetScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成绩查询接口
 */
@RestController
@RequestMapping("/score")
@Tag(name = "成绩接口")
public class CetScoreController {

    @Autowired
    private CetScoreService scoreService;

    @PostMapping("/add")
    @Operation(summary = "手动录入成绩")
    public Result<Void> addScore(@RequestBody CetScore score) {
        scoreService.addScore(score);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的成绩")
    public Result<List> getMyScores() {
        return Result.success(scoreService.getMyScores());
    }

    @GetMapping("/trend")
    @Operation(summary = "成绩趋势")
    public Result<Map> getTrend() {
        return Result.success(scoreService.getScoreTrend());
    }
}
