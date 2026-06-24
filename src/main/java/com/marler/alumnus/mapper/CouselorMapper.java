package com.marler.alumnus.mapper;

import com.marler.alumnus.dto.VCouselorInfo;
import com.marler.alumnus.pojo.Couselor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouselorMapper {

    /**
     * 新增辅导员记录
     */
    int insert(Couselor couselor);

    /**
     * 根据用户ID查询辅导员记录
     */
    Couselor findByUserId(@Param("userId") Integer userId);

    /**
     * 根据教师记录ID查询辅导员记录
     */
    Couselor findByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 根据辅导员记录ID查询
     */
    Couselor findById(@Param("id") Integer id);

    /**
     * 查询所有辅导员完整信息（关联教师、用户档案、学院）
     */
    List<VCouselorInfo> findAllCounselorInfo();

    /**
     * 根据用户ID查询辅导员完整信息
     */
    VCouselorInfo findCounselorInfoByUserId(@Param("userId") Integer userId);
}
