package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VGraduateInfo;
import com.marler.alumnus.pojo.Graduate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GraduateMapper {

    /**
     * 根据用户ID查询毕业生记录
     */
    Graduate findByUserId(@Param("userId") Integer userId);

    /**
     * 根据学生记录ID查询毕业生记录
     */
    Graduate findByStudentId(@Param("studentId") Integer studentId);

    /**
     * 插入毕业生记录
     */
    int insert(Graduate graduate);

    /**
     * 更新毕业生记录
     */
    int updateById(Graduate graduate);

    /**
     * 查询所有毕业生信息（关联学生、学院、专业、班级、档案）
     */
    List<VGraduateInfo> findAll();

    /**
     * 统计毕业生去向分布（按去向分组）
     */
    List<java.util.Map<String, Object>> countByDestination();

    /**
     * 统计毕业生去向分布（按毕业年份和去向分组）
     */
    List<java.util.Map<String, Object>> countByYearAndDestination();

    /**
     * 统计毕业生去向分布（按学院和去向分组）
     */
    List<java.util.Map<String, Object>> countByCollegeAndDestination();

    /**
     * 获取毕业生总数
     */
    Long totalCount();
}
