package com.marler.alumnus.dto;

import lombok.Data;

/**
 * 教师/管理员添加毕业生 DTO
 */
@Data
public class GraduateAddDTO {
    private Integer studentId;           // 学生记录ID
    private Integer graduateYear;        // 毕业年份
    private Integer destination;         // 去向：1-就业，2-考公，3-考研
    private String destinationDetail;    // 去向详情
    private String certificateNo;        // 毕业证书编号
    private Integer teacherId;           // 教师记录ID（管理员使用，指定关联的教师；教师角色自动获取）
    private Integer teacherType;         // 教师类型：1-辅导员，2-导师（管理员使用，教师角色自动获取）
}
