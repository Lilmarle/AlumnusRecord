package com.marler.alumnus.dto;

import lombok.Data;

/**
 * 毕业生信息视图 DTO（映射数据库视图 v_graduate_info）
 * 包含毕业生基本信息、学生信息、用户信息、档案信息、
 * 学院/专业/班级信息、辅导员信息、导师信息
 */
@Data
public class VGraduateInfo {
    // ===== 毕业生基本信息 =====
    private Integer graduateId;           // 毕业记录ID
    private Integer studentId;            // 学生记录ID
    private Integer userId;               // 用户ID
    private Integer graduateYear;         // 毕业年份
    private Integer destination;          // 去向：1-就业，2-考公，3-考研
    private String destinationName;       // 去向名称
    private String destinationDetail;     // 去向详情
    private String certificateNo;         // 毕业证书编号
    private String graduateCreateTime;    // 创建时间
    private String graduateUpdateTime;    // 更新时间

    // ===== 学生基本信息 =====
    private String studentNo;             // 学号
    private String enrollDate;            // 入校时间
    private String studentGraduateDate;   // 学生毕业时间（可为空）

    // ===== 用户信息 =====
    private String username;              // 用户名
    private String phone;                 // 手机号
    private Integer userStatus;           // 用户状态：1-在校，2-离校
    private String statusName;            // 状态名称

    // ===== 档案信息 =====
    private String realName;              // 真实姓名
    private Byte gender;                  // 性别：1-男，2-女，0-未知
    private String genderName;            // 性别名称
    private String avatar;                // 头像URL
    private String idCard;                // 身份证号

    // ===== 学院、专业、班级信息 =====
    private String collegeName;           // 学院名称
    private String majorName;             // 专业名称
    private String className;             // 班级名称
    private String grade;                 // 年级

    // ===== 辅导员信息（teacher_graduate type=1） =====
    private String counselorName;         // 辅导员姓名
    private String counselorAvatar;       // 辅导员头像
    private String counselorTitle;        // 辅导员职称

    // ===== 导师信息（teacher_graduate type=2） =====
    private String supervisorName;        // 导师姓名
    private String supervisorAvatar;      // 导师头像
    private String supervisorTitle;       // 导师职称
}
