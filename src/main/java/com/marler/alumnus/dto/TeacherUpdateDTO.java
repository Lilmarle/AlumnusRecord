package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class TeacherUpdateDTO {
    private Integer id;              // 教师记录ID
    private Integer userId;          // 用户ID（关联user表）
    private Integer collegeId;       // 学院ID（关联college表）
    private String title;            // 职称
    private String employeeNo;       // 工号
    private String hireDate;         // 入职时间
    // 个人信息（可选，更新user_profile表）
    private String name;             // 姓名
    private Byte gender;             // 性别：1-男，2-女，0-未知
    private String avatar;           // 头像URL
    private String idCard;           // 身份证号
}
