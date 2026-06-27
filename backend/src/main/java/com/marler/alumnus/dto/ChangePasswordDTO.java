package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private Integer userId;      // 用户ID
    private String oldPassword;  // 旧密码
    private String newPassword;  // 新密码
}
