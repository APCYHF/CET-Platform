package com.cetprep.controller;

import com.cetprep.advice.Result;
import com.cetprep.dto.AnswerSubmitDTO;
import com.cetprep.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 刷题接口
 */
@RestController
@RequestMapping("/question")
@Tag(name = "刷题接口")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/reading")
    @Operation(summary = "获取阅读题列表")
    public Result<List> getReadingQuestions() {
        return Result.success(questionService.getReadingQuestions());
    }

    @GetMapping("/listening")
    @Operation(summary = "获取听力题列表")
    public Result<List> getListeningQuestions() {
        return Result.success(questionService.getListeningQuestions());
    }

    @GetMapping("/papers")
    @Operation(summary = "获取试卷列表")
    public Result<List> getPapers(@RequestParam(required = false) String level) {
        return Result.success(questionService.getPapers(level));
    }

    @GetMapping("/paper/{paperId}")
    @Operation(summary = "获取试卷详情")
    public Result<Map> getPaperDetail(@PathVariable Long paperId) {
        return Result.success(questionService.getPaperDetail(paperId));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交答案")
    public Result<Map> submitAnswers(@RequestBody AnswerSubmitDTO dto) {
        return Result.success(questionService.submitAnswers(dto));
    }

    @GetMapping("/wrong")
    @Operation(summary = "我的错题本")
    public Result<List> getWrongQuestions() {
        return Result.success(questionService.getWrongQuestions());
    }

    @GetMapping("/{questionId}/words")
    @Operation(summary = "获取题目建议词汇")
    public Result<List> getQuestionWords(@PathVariable Long questionId) {
        return Result.success(questionService.getQuestionWords(questionId));
    }
}
