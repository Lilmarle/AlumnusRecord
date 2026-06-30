package com.marler.alumnus.dto;

import lombok.Data;

/**
 * 学生信息视图 DTO（映射数据库视图 v_student_info）
 * 包含学生基本信息和关联的学院、专业、班级、档案、辅导员、导师信息
 */
@Data
public class VStudentInfo {
    private Integer studentId;        // 学生记录ID
    private Integer userId;           // 用户ID
    private String studentNo;         // 学号
    private String phone;             // 手机号（来自 user）
    private Integer userStatus;       // 用户状态：1-在校，2-离校
    private String statusName;        // 用户状态名称
    private String name;              // 姓名（来自 user_profile）
    private Byte gender;              // 性别：1-男，2-女，0-未知
    private String genderName;        // 性别名称
    private String avatar;            // 头像URL
    private String idCard;            // 身份证号
    private String collegeName;       // 学院名称
    private String majorName;         // 专业名称
    private String className;         // 班级名称
    private String grade;             // 年级
    private String enrollDate;        // 入校时间
    private String graduateDate;      // 毕业时间（可为空）
    private String counselorName;     // 主要辅导员姓名
    private String counselorAvatar;   // 辅导员头像URL
    private String counselorTitle;    // 辅导员职称
    private String supervisorName;    // 主导师（学业导师）姓名
    private String supervisorAvatar;  // 导师头像URL
    private String supervisorTitle;   // 导师职称
}
