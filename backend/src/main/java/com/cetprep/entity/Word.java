package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单词实体
 */
@Data
@TableName("sys_word")
public class Word {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String word;
    private String phonetic;
    private String meaning;
    private String exampleSentence;
    private String root;
    private String level; // CET4, CET6
    private String audioUrl;
    private LocalDateTime createdAt;
}
