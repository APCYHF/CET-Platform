package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体
 */
@Data
@TableName("exam_paper")
public class ExamPaper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String paperType; // REAL, SIMULATION
    private Integer year;
    private String month;
    private String level; // CET4, CET6
    private Integer listenTime;
    private Integer readTime;
    private Integer writeTime;
    private Integer transTime;
    private Integer totalScore;
    private Integer status;
    private LocalDateTime createdAt;
}
