package com.marler.alumnus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    private Integer id;
    private Integer majorId;
    private String className;
    private String code;
    private String grade;
    private String createTime;
    private String updateTime;
}
