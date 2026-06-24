package com.marler.alumnus.service;

import com.marler.alumnus.pojo.CounselorStudent;
import com.marler.alumnus.pojo.Result;

public interface CounselorStudentService {

    /**
     * 添加辅导员-学生关联
     * @param counselorStudent 关联信息
     * @return 操作结果
     */
    Result add(CounselorStudent counselorStudent);

    /**
     * 根据辅导员记录ID查询其管理的学生列表
     * @param couId 辅导员记录ID
     * @return 学生列表
     */
    Result getStudentsByCounselor(Integer couId);

    /**
     * 根据学生记录ID查询其所属的辅导员列表
     * @param stuId 学生记录ID
     * @return 辅导员列表
     */
    Result getCounselorsByStudent(Integer stuId);

    /**
     * 查询所有辅导员-学生关联记录
     * @return 关联记录列表
     */
    Result getAllRelations();

    /**
     * 结束辅导员对学生的管理
     * @param id 关联记录ID
     * @param endDate 结束日期
     * @return 操作结果
     */
    Result endRelation(Integer id, String endDate);

    /**
     * 删除辅导员-学生关联记录
     * @param id 关联记录ID
     * @return 操作结果
     */
    Result deleteRelation(Integer id);
}
