package com.cetprep.vo;

import lombok.Data;

import java.util.List;

/**
 * 题目视图（选项解析为列表）
 */
@Data
public class QuestionVO {
    private Long id;
    private String type;
    private String subType;
    private String content;
    private List<String> options;
    private String answer;
    private String analysis;
    private String listeningText;
    private String audioUrl;
    private String keyWords;
    private Integer difficulty;
    private Integer score;
}
