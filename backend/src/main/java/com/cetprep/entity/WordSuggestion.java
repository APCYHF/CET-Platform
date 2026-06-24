package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目建议词汇
 */
@Data
@TableName("word_suggestion")
public class WordSuggestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long wordId;
    private Integer weight;
    private String remark;
    private LocalDateTime createdAt;
}
