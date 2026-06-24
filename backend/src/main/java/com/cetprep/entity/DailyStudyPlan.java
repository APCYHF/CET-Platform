package com.cetprep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日学习计划
 */
@Data
@TableName("daily_study_plan")
public class DailyStudyPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate planDate;
    private Integer newWords;
    private Integer reviewWords;
    private Integer completed;
    private LocalDateTime createdAt;
}
