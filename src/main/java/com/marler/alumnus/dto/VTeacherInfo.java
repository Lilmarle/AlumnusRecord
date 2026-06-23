package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class VTeacherInfo {
    private Integer teacherId;       // 教师记录ID
    private Integer userId;          // 用户ID
    private String employeeNo;       // 工号
    private String title;            // 职称
    private String hireDate;         // 入职时间
    private String username;         // 用户名
    private String phone;            // 手机号
    private Integer userStatus;      // 用户状态：1-在职，2-离职
    private String statusName;       // 状态名称
    private String realName;         // 真实姓名
    private Byte gender;             // 性别：1-男，2-女，0-未知
    private String genderName;       // 性别名称
    private String avatar;           // 头像URL
    private String idCard;           // 身份证号
    private Integer collegeId;       // 学院ID
    private String collegeName;      // 学院名称
    private String teacherCreateTime;  // 创建时间
    private String teacherUpdateTime;  // 更新时间
}
