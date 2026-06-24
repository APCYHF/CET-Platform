package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成绩记录
 */
@Data
@TableName("cet_score")
public class CetScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String examType; // CET4, CET6
    private LocalDate examDate;
    private Integer totalScore;
    private Integer listenScore;
    private Integer readScore;
    private Integer writeScore;
    private Integer isManual;
    private LocalDateTime createdAt;
}
