package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//CREATE TABLE `graduate` (
//        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '毕业记录ID',
//        `student_id` int(11) NOT NULL COMMENT '学生记录ID（关联student表）',
//        `user_id` int(11) NOT NULL COMMENT '用户ID（关联user表，冗余字段便于查询）',
//        `graduate_year` year(4) NOT NULL COMMENT '毕业年份',
//        `destination` tinyint(1) NOT NULL COMMENT '去向：1-就业，2-考公，3-考研',
//        `destination_detail` varchar(200) DEFAULT NULL COMMENT '去向详情（如：就业单位名称、考研学校名称等）',
//        `certificate_no` varchar(50) DEFAULT NULL COMMENT '毕业证书编号',
//        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//PRIMARY KEY (`id`),
//UNIQUE KEY `uk_student_id` (`student_id`),
//KEY `idx_user_id` (`user_id`),
//KEY `idx_graduate_year` (`graduate_year`),
//KEY `idx_destination` (`destination`),
//CONSTRAINT `fk_graduate_student_id` FOREIGN KEY (`student_id`) REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
//CONSTRAINT `fk_graduate_user_id` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='毕业生信息表';
public class Graduate {
    private Integer id;                  // 毕业记录ID
    private Integer studentId;           // 学生记录ID（关联student表）
    private Integer userId;              // 用户ID（关联user表，冗余字段便于查询）
    private Integer graduateYear;        // 毕业年份
    private Integer destination;         // 去向：1-就业，2-考公，3-考研
    private String destinationDetail;    // 去向详情（如：就业单位名称、考研学校名称等）
    private String certificateNo;        // 毕业证书编号
    private String createTime;           // 创建时间
    private String updateTime;           // 更新时间
}
