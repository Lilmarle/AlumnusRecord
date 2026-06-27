package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `counselor_student` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
//        `cou_id` int(11) NOT NULL COMMENT '辅导员记录ID（关联counselor表）',
//        `stu_id` int(11) NOT NULL COMMENT '学生记录ID（关联student表）',
//        `start_date` date NOT NULL COMMENT '开始管理日期',
//        `end_date` date DEFAULT NULL COMMENT '结束管理日期（NULL表示当前仍在管理中）',
//        `is_primary` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否主要辅导员：1-主要，0-次要',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_cou_stu_active` (`cou_id`, `stu_id`, `end_date`),
//KEY `idx_stu_id` (`stu_id`),
//KEY `idx_start_date` (`start_date`),
//KEY `idx_is_primary` (`is_primary`),
//CONSTRAINT `fk_cs_cou_id` FOREIGN KEY (`cou_id`) REFERENCES `counselor`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_cs_stu_id` FOREIGN KEY (`stu_id`) REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅导员学生关联表';
public class CounselorStudent {
    private Integer id;
    private Integer couId;
    private Integer stuId;
    private String startDate;
    private String endDate;
    private Boolean isPrimary;
    private String createTime;
    private String updateTime;
}
