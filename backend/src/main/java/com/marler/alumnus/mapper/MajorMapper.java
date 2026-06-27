package com.marler.alumnus.mapper;

import com.marler.alumnus.pojo.Major;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorMapper {

    /**
     * 查询所有专业
     */
    List<Major> findAll();
}
