package com.cetprep.dto;

import lombok.Data;

import java.util.List;

/**
 * 提交答案请求
 */
@Data
public class AnswerSubmitDTO {
    private Long paperId;
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
        private Integer spendTime;
    }
}
