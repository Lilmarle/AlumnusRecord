package com.marler.alumnus.service;

import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.pojo.Result;

public interface GraduateService {

    /**
     * 提交毕业生信息
     * 需要检查学生年级是否为4（大四）
     */
    Result submit(GraduateSubmitDTO dto, Integer userId);

    /**
     * 根据用户ID查询毕业生记录
     */
    Result getByUserId(Integer userId);

    /**
     * 查询所有毕业生信息（教师/管理员使用）
     */
    Result getAllGraduates();

    /**
     * 获取毕业生去向统计（教师/管理员使用）
     * 返回按去向、年份、学院分组统计数据
     */
    Result getDestinationStatistics();
}
