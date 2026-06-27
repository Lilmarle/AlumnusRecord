package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id;              // 学生记录ID
    private Integer userId;          // 用户ID（关联user表）
    private String studentNo;        // 学号
    private Integer collegeId;       // 学院ID（关联college表）
    private Integer majorId;         // 专业ID（关联major表）
    private Integer classId;         // 班级ID（关联class表）
    private String enrollDate;       // 入校时间（年月日）
    private String graduateDate;     // 毕业时间（年月日，可为空）
    private String createTime;       // 创建时间
    private String updateTime;       // 更新时间
}
