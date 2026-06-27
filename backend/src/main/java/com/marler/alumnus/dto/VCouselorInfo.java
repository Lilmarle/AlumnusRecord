package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class VCouselorInfo {
    private Integer counselorId;         // 辅导员记录ID
    private Integer teacherId;           // 教师记录ID
    private Integer userId;              // 用户ID
    private String counselorHireDate;    // 担任辅导员日期
    private Boolean isActive;            // 是否在职
    private String isActiveName;         // 在职状态名称
    private String employeeNo;           // 工号
    private String title;                // 职称
    private String teacherHireDate;      // 入职时间
    private String username;             // 用户名
    private String phone;                // 手机号
    private Integer userStatus;          // 用户状态：1-正常，0-禁用
    private String statusName;           // 用户状态名称
    private String realName;             // 真实姓名
    private Byte gender;                 // 性别：1-男，2-女，0-未知
    private String genderName;           // 性别名称
    private String avatar;               // 头像URL
    private String idCard;               // 身份证号
    private Integer collegeId;           // 学院ID
    private String collegeName;          // 学院名称
    private String counselorCreateTime;  // 创建时间
    private String counselorUpdateTime;  // 更新时间
}
