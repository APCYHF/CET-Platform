package com.cetprep.vo;

import lombok.Data;

/**
 * 用户信息视图
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String school;
    private String grade;
    private Integer targetScore;
    private Integer studyDuration;
    private Integer points;
    private String role;
}
