package com.cetprep.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 注册请求
 */
@Data
public class RegisterDTO {
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度4-20位")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;
    private String nickname;
}
