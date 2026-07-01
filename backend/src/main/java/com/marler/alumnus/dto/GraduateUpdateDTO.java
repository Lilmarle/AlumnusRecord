package com.marler.alumnus.dto;

import lombok.Data;

/**
 * 修改毕业生信息 DTO
 */
@Data
public class GraduateUpdateDTO {
    private Integer id;                  // 毕业记录ID
    private Integer graduateYear;        // 毕业年份
    private Integer destination;         // 去向：1-就业，2-考公，3-考研
    private String destinationDetail;    // 去向详情
    private String certificateNo;        // 毕业证书编号
}
