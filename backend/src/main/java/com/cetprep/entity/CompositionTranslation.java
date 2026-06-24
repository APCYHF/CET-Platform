package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作文/翻译提交
 */
@Data
@TableName("composition_translation")
public class CompositionTranslation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type; // WRITING, TRANSLATION
    private String title;
    private String content;
    private String imageUrl;
    private Integer aiScore;
    private String aiFeedback;
    private String manualStatus; // NONE, PENDING, ASSIGNED, COMPLETED
    private String manualFeedback;
    private String manualImage;
    private Long teacherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
