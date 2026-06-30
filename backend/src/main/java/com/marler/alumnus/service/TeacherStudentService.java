package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;

public interface TeacherStudentService {

    /**
     * 根据教师ID和类型查询关联的学生列表
     * @param teacherId 教师记录ID
     * @param type 类型：1-辅导员，2-导师
     */
    Result getStudentsByTeacher(Integer teacherId, Integer type);
}
