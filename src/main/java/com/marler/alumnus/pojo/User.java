package com.marler.alumnus.pojo;

import lombok.Data;

//
//CREATE TABLE `user` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
//        `username` varchar(50) NOT NULL COMMENT '用户名',
//        `password` varchar(255) NOT NULL COMMENT '密码（加密存储）',
//        `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
//        `role` tinyint(1) NOT NULL DEFAULT 1 COMMENT '角色：1-学生，2-老师，3-管理员',
//        `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1-在校，2-离校',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_username` (`username`),
//KEY `idx_phone` (`phone`)
//        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
@Data
public class User {
    private Integer id;        // 用户ID
    private String username;   // 用户名
    private String password;   // 密码（加密存储）
    private String phone;      // 手机号
    private Integer role;      // 角色：1-学生，2-老师，3-管理员
    private Integer status;    // 状态：1-在校，2-离校
    private String createTime; // 创建时间
    private String updateTime; // 更新时间
}
