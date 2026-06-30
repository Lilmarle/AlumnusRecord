package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VGraduateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherGraduateMapper {

    /**
     * 根据教师记录ID查询该教师关联的所有毕业生信息
     */
    List<VGraduateInfo> findGraduatesByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 根据用户ID查询该教师关联的所有毕业生信息
     */
    List<VGraduateInfo> findGraduatesByUserId(@Param("userId") Integer userId);
}
