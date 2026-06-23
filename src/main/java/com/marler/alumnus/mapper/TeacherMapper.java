package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VTeacherInfo;
import com.marler.alumnus.pojo.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper {

    /**
     * 根据用户ID查询教师信息
     */
    Teacher findByUserId(@Param("userId") Integer userId);

    /**
     * 根据教师记录ID查询
     */
    Teacher findById(@Param("id") Integer id);

    /**
     * 更新教师信息
     */
    int update(Teacher teacher);

    /**
     * 根据用户ID查询完整的教师信息（关联学院、档案）
     */
    VTeacherInfo findTeacherInfoByUserId(@Param("userId") Integer userId);

    /**
     * 查询所有教师的完整信息（管理员使用）
     */
    List<VTeacherInfo> findAllTeacherInfo();
}
