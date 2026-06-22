package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;   // 用户名
    private String password;   // 密码
    private String phone;      // 手机号
    private Integer role;      // 角色：1-学生，2-老师，3-管理员
}
