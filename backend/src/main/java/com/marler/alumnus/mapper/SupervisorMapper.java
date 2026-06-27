package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VSupervisorInfo;
import com.marler.alumnus.pojo.Supervisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupervisorMapper {

    /**
     * 新增导师记录
     */
    int insert(Supervisor supervisor);

    /**
     * 根据ID查询
     */
    Supervisor findById(@Param("id") Integer id);

    /**
     * 根据用户ID查询导师记录
     */
    Supervisor findByUserId(@Param("userId") Integer userId);

    /**
     * 根据教师ID查询导师记录
     */
    Supervisor findByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 查询所有导师完整信息
     */
    List<VSupervisorInfo> findAll();

    /**
     * 根据用户ID查询导师完整信息
     */
    VSupervisorInfo findByUserIdFull(@Param("userId") Integer userId);
}
