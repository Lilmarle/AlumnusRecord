package com.marler.alumnus.service;

import com.marler.alumnus.dto.TeacherUpdateDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;

public interface TeacherService {

    /**
     * 修改教师信息（支持同时更新 teacher 表和 user_profile 表）
     */
    Result update(TeacherUpdateDTO teacherUpdateDTO);

    /**
     * 根据教师记录ID查询
     */
    Teacher findTeacherById(Integer id);

    /**
     * 根据用户ID查询完整的教师信息（包含学院名称、档案）
     */
    Result getTeacherInfo(Integer userId, Integer tokenUserId, Integer role);

    /**
     * 查询所有教师列表（管理员使用）
     */
    Result getAllTeachers();
}
