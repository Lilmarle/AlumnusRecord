package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//CREATE TABLE `counselor` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '辅导员记录ID',
//        `teacher_id` int(11) NOT NULL COMMENT '教师记录ID（关联teacher表）',
//        `user_id` int(11) NOT NULL COMMENT '用户ID（关联user表，冗余字段便于查询）',
//        `hire_date` date DEFAULT NULL COMMENT '担任辅导员日期',
//        `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否在职：1-在职，0-离职',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_teacher_id` (`teacher_id`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_is_active` (`is_active`),
//CONSTRAINT `fk_counselor_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_counselor_user_id` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅导员信息表';
public class Couselor {
    private Integer id;
    private Integer teacherId;
    private Integer userId;
    private String hireDate;
    private Boolean isActive;
    private String createTime;
    private String updateTime;
}
