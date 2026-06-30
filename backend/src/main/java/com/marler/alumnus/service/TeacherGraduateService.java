package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;

public interface TeacherGraduateService {

    /**
     * 根据教师记录ID查询该教师关联的所有毕业生信息
     */
    Result getGraduatesByTeacherId(Integer teacherId);

    /**
     * 根据当前登录用户ID查询其教师身份关联的毕业生信息
     */
    Result getMyGraduates(Integer userId);
}
