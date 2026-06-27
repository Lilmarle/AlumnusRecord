package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;   // 用户名或手机号
    private String password;  // 密码
}
