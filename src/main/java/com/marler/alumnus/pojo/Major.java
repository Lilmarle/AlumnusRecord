package com.marler.alumnus.pojo;

//CREATE TABLE `major` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '专业ID',
//        `college_id` int(11) NOT NULL COMMENT '所属学院ID',
//        `major_name` varchar(100) NOT NULL COMMENT '专业名称',
//        `code` varchar(20) DEFAULT NULL COMMENT '专业代号（可选）',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_major_name` (`major_name`),
//KEY `idx_college_id` (`college_id`),
//KEY `idx_code` (`code`),
//CONSTRAINT `fk_major_college_id` FOREIGN KEY (`college_id`) REFERENCES `college`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';
public class Major {
}
