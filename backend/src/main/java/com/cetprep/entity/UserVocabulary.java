package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户生词本
 */
@Data
@TableName("user_vocabulary")
public class UserVocabulary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long wordId;
    private String status; // NEW, REVIEW, MASTERED
    private Integer proficiency; // 0-2
    private LocalDate nextReviewDate;
    private String addSource; // MANUAL, QUESTION, RECOMMEND
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
