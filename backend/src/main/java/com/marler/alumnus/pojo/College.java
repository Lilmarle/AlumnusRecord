package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `college` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学院ID',
//        `college_name` varchar(100) NOT NULL COMMENT '学院名称',
//        `code` varchar(20) DEFAULT NULL COMMENT '学院代号（可选）',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_college_name` (`college_name`),
//KEY `idx_code` (`code`)
//        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';
public class College {
    private Integer id;                   // 学院ID
    private String collegeName;           // 学院名称
    private String code;                  // 学院代号（可选）
    private String createTime;            // 创建时间
    private String updateTime;            // 更新时间
}
