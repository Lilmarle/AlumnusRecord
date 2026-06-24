package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.CounselorStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CounselorStudentMapper {

    /**
     * 新增辅导员-学生关联记录
     */
    int insert(CounselorStudent counselorStudent);

    /**
     * 根据关联记录ID查询
     */
    CounselorStudent findById(@Param("id") Integer id);

    /**
     * 根据辅导员记录ID查询关联的学生列表
     */
    List<Map<String, Object>> findStudentsByCouId(@Param("couId") Integer couId);

    /**
     * 根据学生记录ID查询关联的辅导员列表
     */
    List<Map<String, Object>> findCounselorsByStuId(@Param("stuId") Integer stuId);

    /**
     * 查询所有辅导员-学生关联记录
     */
    List<Map<String, Object>> findAllRelations();

    /**
     * 检查是否已存在关联（同一辅导员、同一学生、且未结束管理）
     */
    CounselorStudent findActiveRelation(@Param("couId") Integer couId, @Param("stuId") Integer stuId);

    /**
     * 更新关联记录（如结束管理日期）
     */
    int update(CounselorStudent counselorStudent);

    /**
     * 删除关联记录
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据辅导员记录ID查询该辅导员管理的所有学生记录ID
     */
    List<Integer> findStuIdsByCouId(@Param("couId") Integer couId);
}
