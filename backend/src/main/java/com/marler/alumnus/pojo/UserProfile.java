package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `user_profile` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '档案ID',
//        `user_id` int(11) NOT NULL COMMENT '用户ID（关联user表）',
//        `name` varchar(50) NOT NULL COMMENT '姓名',
//        `gender` tinyint(1) DEFAULT NULL COMMENT '性别：1-男，2-女，0-未知',
//        `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
//        `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号（可为空）',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_user_id` (`user_id`),
//UNIQUE KEY `uk_id_card` (`id_card`),
//KEY `idx_name` (`name`)
//        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户档案表';
public class UserProfile {
    private Integer id;          // 档案ID
    private Integer userId;      // 用户ID（关联user表）
    private String name;         // 姓名
    private Byte gender;         // 性别：1-男，2-女，0-未知
    private String avatar;       // 头像URL
    private String idCard;       // 身份证号（可为空）
    private String createTime;   // 创建时间
    private String updateTime;   // 更新时间
}
