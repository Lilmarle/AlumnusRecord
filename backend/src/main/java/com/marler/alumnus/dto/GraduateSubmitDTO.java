package com.marler.alumnus.dto;

import lombok.Data;

@Data
public class GraduateSubmitDTO {
    private Integer studentId;           // 学生记录ID
    private Integer graduateYear;        // 毕业年份
    private Integer destination;         // 去向：1-就业，2-考公，3-考研
    private String destinationDetail;    // 去向详情
    private String certificateNo;        // 毕业证书编号
}
