package com.cetprep.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cetprep.advice.Result;
import com.cetprep.entity.*;
import com.cetprep.mapper.*;
import com.cetprep.service.UserService;
import com.cetprep.util.AiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理接口
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "后台管理")
public class AdminController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamPaperMapper examPaperMapper;
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;
    @Autowired
    private MallOrderMapper orderMapper;
    @Autowired
    private UserAnswerRecordMapper answerRecordMapper;
    @Autowired
    private CompositionTranslationMapper compositionMapper;
    @Autowired
    private AiClient aiClient;

    // ========== 用户管理 ==========
    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public Result<Page> userList(@RequestParam(defaultValue = "1") int current,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(userMapper.selectPage(new Page<>(current, size), new QueryWrapper<User>().orderByDesc("created_at")));
    }

    @PutMapping("/user/{id}/status")
    @Operation(summary = "修改用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus(status);
            userMapper.updateById(user);
        }
        return Result.success();
    }

    // ========== 题库管理 ==========
    @PostMapping("/question")
    @Operation(summary = "添加题目")
    public Result<Void> addQuestion(@RequestBody Question question) {
        questionMapper.insert(question);
        return Result.success();
    }

    @GetMapping("/questions")
    @Operation(summary = "题目列表")
    public Result<Page> questionList(@RequestParam(defaultValue = "1") int current,
                                       @RequestParam(defaultValue = "10") int size) {
        return Result.success(questionMapper.selectPage(new Page<>(current, size), new QueryWrapper<Question>().orderByDesc("created_at")));
    }

    @DeleteMapping("/question/{id}")
    @Operation(summary = "删除题目")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/paper")
    @Operation(summary = "创建试卷")
    public Result<Void> createPaper(@RequestBody ExamPaper paper) {
        examPaperMapper.insert(paper);
        return Result.success();
    }

    @PostMapping("/paper/{paperId}/question")
    @Operation(summary = "试卷添加题目")
    public Result<Void> addPaperQuestion(@PathVariable Long paperId, @RequestBody PaperQuestion pq) {
        pq.setPaperId(paperId);
        paperQuestionMapper.insert(pq);
        return Result.success();
    }

    @PostMapping("/question/{id}/extract-words")
    @Operation(summary = "AI提取题目词汇")
    public Result<String> extractWords(@PathVariable Long id) {
        Question q = questionMapper.selectById(id);
        if (q == null) return Result.error("题目不存在");
        String text = (q.getContent() != null ? q.getContent() : "") + " " + (q.getListeningText() != null ? q.getListeningText() : "");
        String result = aiClient.extractKeyWords(text);
        return Result.success(result);
    }

    // ========== 订单管理 ==========
    @GetMapping("/orders")
    @Operation(summary = "订单列表")
    public Result<Page> orderList(@RequestParam(defaultValue = "1") int current,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderMapper.selectPage(new Page<>(current, size), new QueryWrapper<MallOrder>().orderByDesc("created_at")));
    }

    @PutMapping("/order/{id}/status")
    @Operation(summary = "修改订单状态")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        MallOrder order = orderMapper.selectById(id);
        if (order != null) {
            order.setStatus(status);
            orderMapper.updateById(order);
        }
        return Result.success();
    }

    // ========== 批改任务分配 ==========
    @GetMapping("/correction/pending")
    @Operation(summary = "待批改列表")
    public Result<List> pendingCorrections() {
        return Result.success(compositionMapper.selectList(
                new QueryWrapper<CompositionTranslation>()
                        .eq("manual_status", "PENDING")
                        .orderByDesc("created_at")));
    }

    @PutMapping("/correction/{id}/assign")
    @Operation(summary = "分配批改任务")
    public Result<Void> assignCorrection(@PathVariable Long id, @RequestParam Long teacherId) {
        CompositionTranslation ct = compositionMapper.selectById(id);
        if (ct != null) {
            ct.setManualStatus("ASSIGNED");
            ct.setTeacherId(teacherId);
            compositionMapper.updateById(ct);
        }
        return Result.success();
    }

    // ========== 数据看板 ==========
    @GetMapping("/dashboard")
    @Operation(summary = "数据看板")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.selectCount(null));
        data.put("totalQuestions", questionMapper.selectCount(null));
        data.put("totalOrders", orderMapper.selectCount(null));
        data.put("todayAnswers", answerRecordMapper.selectCount(
                new QueryWrapper<UserAnswerRecord>().apply("DATE(answer_date) = CURDATE()")));
        return Result.success(data);
    }
}
