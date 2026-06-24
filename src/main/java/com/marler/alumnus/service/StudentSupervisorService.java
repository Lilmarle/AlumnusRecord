package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.StudentSupervisor;

public interface StudentSupervisorService {

    /**
     * 添加导师-学生关联
     */
    Result add(StudentSupervisor studentSupervisor);

    /**
     * 根据导师记录ID查询其指导的学生列表
     */
    Result getStudentsBySupervisor(Integer supId);

    /**
     * 根据学生记录ID查询其所属的导师列表
     */
    Result getSupervisorsByStudent(Integer stuId);

    /**
     * 查询所有导师-学生关联记录
     */
    Result getAllRelations();

    /**
     * 结束导师对学生的指导
     */
    Result endRelation(Integer id, String endDate);

    /**
     * 删除导师-学生关联记录
     */
    Result deleteRelation(Integer id);
}
