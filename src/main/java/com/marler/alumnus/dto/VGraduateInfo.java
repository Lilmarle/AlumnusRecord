package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class VGraduateInfo {
    private Integer id;                  // 毕业记录ID
    private Integer studentId;           // 学生记录ID
    private Integer userId;              // 用户ID
    private Integer graduateYear;        // 毕业年份
    private Integer destination;         // 去向：1-就业，2-考公，3-考研
    private String destinationDetail;    // 去向详情
    private String certificateNo;        // 毕业证书编号
    // 学生信息
    private String studentNo;            // 学号
    private String name;                 // 姓名
    private String collegeName;          // 学院名称
    private String majorName;            // 专业名称
    private String className;            // 班级名称
    private String grade;                // 年级
    private String createTime;           // 创建时间
    private String updateTime;           // 更新时间
}
