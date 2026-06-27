package com.marler.alumnus.service;

import com.marler.alumnus.dto.StudentUpdateDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;

public interface StudentService {

    /**
     * 添加学生
     * @param student 学生信息
     * @return 操作结果
     */
    Result add(Student student);

    /**
     * 修改学生信息（支持同时更新 student 表和 user_profile 表）
     */
    Result update(StudentUpdateDTO studentUpdateDTO);

    /**
     * 根据学生记录ID查询
     */
    Student findStudentById(Integer id);

    /**
     * 根据用户ID查询完整的学生信息（包含学院、专业、班级、档案）
     * 学生角色只允许查自己的信息
     */
    Result getStudentInfo(Integer userId, Integer tokenUserId, Integer role);

    /**
     * 查询所有学生列表（教师/管理员使用）
     */
    Result getAllStudents();
}
