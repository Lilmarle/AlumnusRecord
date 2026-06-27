package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `supervisor` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '导师记录ID',
//        `teacher_id` int(11) NOT NULL COMMENT '教师记录ID（关联teacher表）',
//        `user_id` int(11) NOT NULL COMMENT '用户ID（关联user表，冗余字段便于查询）',
//        `supervisor_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '导师类型：1-学业导师，2-论文导师，3-实习导师，4-班主任',
//        `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否在职：1-在职，0-离职',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_teacher_type` (`teacher_id`, `supervisor_type`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_supervisor_type` (`supervisor_type`),
//KEY `idx_is_active` (`is_active`),
//CONSTRAINT `fk_supervisor_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_supervisor_user_id` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导师信息表';
public class Supervisor {
    private Integer id;
    private Integer teacherId;
    private Integer userId;
    private Integer supervisorType;
    private Boolean isActive;
    private String createTime;
    private String updateTime;
}
