package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.TeacherStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherStudentMapper {

    /**
     * 根据教师ID查询关联的学生列表
     * @param teacherId 教师记录ID
     */
    List<Map<String, Object>> findStudentsByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 根据学生记录ID和教师记录ID查询关联关系
     * @param studentId 学生记录ID
     * @param teacherId 教师记录ID
     * @return 教师学生关联记录
     */
    TeacherStudent findByStudentIdAndTeacherId(@Param("studentId") Integer studentId, @Param("teacherId") Integer teacherId);
}
