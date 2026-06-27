package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `student_supervisor` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
//        `sup_id` int(11) NOT NULL COMMENT '导师记录ID（关联supervisor表）',
//        `stu_id` int(11) NOT NULL COMMENT '学生记录ID（关联student表）',
//        `start_date` date NOT NULL COMMENT '开始指导日期',
//        `end_date` date DEFAULT NULL COMMENT '结束指导日期（NULL表示当前仍在指导中）',
//        `is_primary` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否主要导师：1-主要，0-次要',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_sup_stu_active` (`sup_id`, `stu_id`, `end_date`),
//KEY `idx_stu_id` (`stu_id`),
//KEY `idx_start_date` (`start_date`),
//KEY `idx_is_primary` (`is_primary`),
//CONSTRAINT `fk_ss_sup_id` FOREIGN KEY (`sup_id`) REFERENCES `supervisor`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_ss_stu_id` FOREIGN KEY (`stu_id`) REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导师学生关联表';
public class StudentSupervisor {
    private Integer id;
    private Integer supId;
    private Integer stuId;
    private String startDate;
    private String endDate;
    private Boolean isPrimary;
    private String createTime;
    private String updateTime;
}
