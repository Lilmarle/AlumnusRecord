package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.Class;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassMapper {

    /**
     * 查询所有班级
     */
    List<Class> findAll();
}
