package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;

public interface TeacherStudentService {

    /**
     * 根据教师ID查询关联的学生列表
     * @param teacherId 教师记录ID
     */
    Result getStudentsByTeacher(Integer teacherId);
}
