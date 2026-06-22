package com.marler.alumnus.dto;

import lombok.Data;

/**
 * 学生信息视图 DTO（映射数据库视图 v_student_info）
 * 包含学生基本信息和关联的学院、专业、班级、档案信息
 */
@Data
public class VStudentInfo {
    private Integer studentId;        // 学生记录ID
    private Integer userId;           // 用户ID
    private String studentNo;         // 学号
    private Integer collegeId;        // 学院ID
    private String collegeName;       // 学院名称
    private Integer majorId;          // 专业ID
    private String majorName;         // 专业名称
    private Integer classId;           // 班级ID
    private String className;         // 班级名称
    private String grade;             // 年级
    private String name;              // 姓名（来自 user_profile）
    private Byte gender;              // 性别：1-男，2-女，0-未知
    private String avatar;            // 头像URL
    private String idCard;            // 身份证号
    private String enrollDate;        // 入校时间
    private String graduateDate;      // 毕业时间（可为空）
    private String createTime;        // 创建时间
    private String updateTime;        // 更新时间
}
