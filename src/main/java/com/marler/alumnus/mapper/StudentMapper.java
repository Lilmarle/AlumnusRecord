package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VStudentInfo;
import com.marler.alumnus.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {

    /**
     * 新增学生记录
     */
    int insert(Student student);

    /**
     * 根据用户ID查询学生信息
     */
    Student findByUserId(@Param("userId") Integer userId);

    /**
     * 根据学生记录ID查询
     */
    Student findById(@Param("id") Integer id);

    /**
     * 更新学生信息
     */
    int update(Student student);

    /**
     * 根据用户ID查询完整的学生信息（关联学院、专业、班级、档案）
     */
    VStudentInfo findStudentInfoByUserId(@Param("userId") Integer userId);

    /**
     * 查询所有学生的完整信息（教师/管理员使用）
     */
    List<VStudentInfo> findAllStudentInfo();
}
