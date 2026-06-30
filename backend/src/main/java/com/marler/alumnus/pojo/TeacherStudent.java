package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `teacher_student` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
//        `student_id` int(11) NOT NULL COMMENT '学生记录ID（关联student表）',
//        `teacher_id` int(11) NOT NULL COMMENT '教师记录ID（关联teacher表）',
//        `type` tinyint(1) NOT NULL COMMENT '类型：1-辅导员，2-导师',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_student_teacher` (`student_id`, `teacher_id`),
//KEY `idx_teacher_id` (`teacher_id`),
//KEY `idx_type` (`type`),
//CONSTRAINT `fk_ts_student_id` FOREIGN KEY (`student_id`) REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_ts_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老师指导学生关联表';
public class TeacherStudent {
    private Integer id;
    private Integer studentId;
    private Integer teacherId;
    private Integer type;
    private String createTime;
    private String updateTime;
}
