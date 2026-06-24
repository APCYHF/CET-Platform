package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type; // LISTENING, READING, WRITING, TRANSLATION
    private String subType;
    private String content;
    private String options; // JSON
    private String answer;
    private String analysis;
    private String listeningText;
    private String audioUrl;
    private String audioSegments; // JSON
    private String keyWords;
    private Integer difficulty;
    private Integer score;
    private LocalDateTime createdAt;
}
