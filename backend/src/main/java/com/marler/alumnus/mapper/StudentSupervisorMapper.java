package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.StudentSupervisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentSupervisorMapper {

    /**
     * 新增导师-学生关联记录
     */
    int insert(StudentSupervisor studentSupervisor);

    /**
     * 根据关联记录ID查询
     */
    StudentSupervisor findById(@Param("id") Integer id);

    /**
     * 根据导师记录ID查询关联的学生列表
     */
    List<Map<String, Object>> findStudentsBySupId(@Param("supId") Integer supId);

    /**
     * 根据学生记录ID查询关联的导师列表
     */
    List<Map<String, Object>> findSupervisorsByStuId(@Param("stuId") Integer stuId);

    /**
     * 查询所有导师-学生关联记录
     */
    List<Map<String, Object>> findAllRelations();

    /**
     * 检查是否已存在关联（同一导师、同一学生、且未结束指导）
     */
    StudentSupervisor findActiveRelation(@Param("supId") Integer supId, @Param("stuId") Integer stuId);

    /**
     * 更新关联记录（如结束指导日期）
     */
    int update(StudentSupervisor studentSupervisor);

    /**
     * 删除关联记录
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据导师记录ID查询该导师指导的所有学生记录ID
     */
    List<Integer> findStuIdsBySupId(@Param("supId") Integer supId);
}
