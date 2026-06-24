package com.cetprep.vo;

import lombok.Data;

/**
 * 单词视图
 */
@Data
public class WordVO {
    private Long id;
    private String word;
    private String phonetic;
    private String meaning;
    private String exampleSentence;
    private String root;
    private String level;
    private String audioUrl;
}
