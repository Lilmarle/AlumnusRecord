package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class StudentUpdateDTO {
    private Integer id;              // 学生记录ID
    private Integer userId;          // 用户ID（关联user表）
    private String studentNo;        // 学号
    private Integer collegeId;       // 学院ID（关联college表）
    private Integer majorId;         // 专业ID（关联major表）
    private Integer classId;         // 班级ID（关联class表）
    private String enrollDate;       // 入校时间（年月日）
    private String graduateDate;     // 毕业时间（年月日，可为空）
    // 个人信息（可选，更新user_profile表）
    private String name;             // 姓名
    private Byte gender;             // 性别：1-男，2-女，0-未知
    private String avatar;           // 头像URL
    private String idCard;           // 身份证号
}
