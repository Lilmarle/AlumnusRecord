package com.marler.alumnus.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherStudentMapper {

    /**
     * 根据教师ID和类型查询关联的学生列表
     * @param teacherId 教师记录ID
     * @param type 类型：1-辅导员，2-导师
     */
    List<Map<String, Object>> findStudentsByTeacherId(@Param("teacherId") Integer teacherId, @Param("type") Integer type);
}
