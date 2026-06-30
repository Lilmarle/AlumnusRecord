package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `teacher_graduate` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
//        `teacher_id` int(11) NOT NULL COMMENT '教师记录ID（关联teacher表）',
//        `graduate_id` int(11) NOT NULL COMMENT '毕业生记录ID（关联graduate表）',
//        `type` tinyint(1) NOT NULL COMMENT '类型：1-辅导员，2-导师',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_teacher_graduate` (`teacher_id`, `graduate_id`),
//KEY `idx_graduate_id` (`graduate_id`),
//KEY `idx_type` (`type`),
//CONSTRAINT `fk_tg_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_tg_graduate_id` FOREIGN KEY (`graduate_id`) REFERENCES `graduate`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师毕业生关联表';
public class TeacherGraduate {
    private Integer id;            // 关联记录ID
    private Integer teacherId;     // 教师记录ID（关联teacher表）
    private Integer graduateId;    // 毕业生记录ID（关联graduate表）
    private Integer type;          // 类型：1-辅导员，2-导师
    private String createTime;     // 创建时间
    private String updateTime;     // 更新时间
}
