package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户做题记录
 */
@Data
@TableName("user_answer_record")
public class UserAnswerRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long paperId;
    private Long questionId;
    private String userAnswer;
    private Integer isCorrect; // 0-错 1-对
    private Integer spendTime;
    private LocalDateTime answerDate;
}
