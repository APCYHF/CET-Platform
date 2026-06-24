package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 单词/生词本/背单词接口
 */
@RestController
@RequestMapping("/word")
@Tag(name = "单词接口")
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping("/list")
    @Operation(summary = "单词列表")
    public Result<List> getWords(@RequestParam(required = false) String level,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return Result.success(wordService.getWordList(level, page, size));
    }

    @GetMapping("/today-plan")
    @Operation(summary = "今日学习计划")
    public Result<Map> getTodayPlan() {
        return Result.success(wordService.getTodayStudyPlan());
    }

    @PostMapping("/status/{wordId}")
    @Operation(summary = "更新单词学习状态")
    public Result<Void> updateStatus(@PathVariable Long wordId, @RequestParam String status) {
        wordService.updateWordStatus(wordId, status);
        return Result.success();
    }

    @PostMapping("/vocabulary/batch")
    @Operation(summary = "批量加入生词本")
    public Result<Void> addToVocabulary(@RequestBody List<Long> wordIds) {
        wordService.addWordsToVocabulary(wordIds);
        return Result.success();
    }

    @GetMapping("/vocabulary/my")
    @Operation(summary = "我的生词本")
    public Result<List> getMyVocabulary() {
        return Result.success(wordService.getMyVocabulary());
    }

    @GetMapping("/stats")
    @Operation(summary = "学习统计")
    public Result<Map> getStats() {
        return Result.success(wordService.getStudyStats());
    }
}
