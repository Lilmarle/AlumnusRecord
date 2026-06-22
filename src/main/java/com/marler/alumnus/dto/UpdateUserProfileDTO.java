package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class UpdateUserProfileDTO {
    private Integer userId;   // 用户ID
    private String name;      // 姓名
    private Byte gender;      // 性别：1-男，2-女，0-未知
    private String avatar;    // 头像URL
    private String idCard;    // 身份证号
}
