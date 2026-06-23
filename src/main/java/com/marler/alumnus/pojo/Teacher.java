package com.marler.alumnus.pojo;

import lombok.Data;

@Data
public class Teacher {
    private Integer id;          // 教师记录ID
    private Integer userId;      // 用户ID
    private Integer collegeId;   // 所属学院ID
    private String title;        // 职称
    private String employeeNo;   // 工号
    private String hireDate;     // 入职时间
    private String createTime;   // 创建时间
    private String updateTime;   // 更新时间
}
