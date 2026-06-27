package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.College;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollegeMapper {

    /**
     * 查询所有学院
     */
    List<College> findAll();
}
