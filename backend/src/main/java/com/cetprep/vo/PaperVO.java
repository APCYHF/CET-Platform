package com.cetprep.vo;

import lombok.Data;

import java.util.List;

/**
 * 试卷详情视图
 */
@Data
public class PaperVO {
    private Long id;
    private String title;
    private String paperType;
    private Integer year;
    private String month;
    private String level;
    private List<QuestionVO> listeningQuestions;
    private List<QuestionVO> readingQuestions;
    private List<QuestionVO> writingQuestions;
    private List<QuestionVO> translationQuestions;
}
