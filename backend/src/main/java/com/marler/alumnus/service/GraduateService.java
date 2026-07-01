package com.marler.alumnus.service;

import com.marler.alumnus.dto.GraduateAddDTO;
import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.dto.GraduateUpdateDTO;
import com.marler.alumnus.pojo.Result;

public interface GraduateService {

    /**
     * 提交毕业生信息
     * 需要检查学生年级是否为4（大四）
     */
    Result submit(GraduateSubmitDTO dto, Integer userId);

    /**
     * 教师/管理员添加毕业生
     * @param dto 添加毕业生请求参数
     * @param operatorUserId 操作用户ID
     * @param operatorRole 操作用户角色：2-教师，3-管理员
     */
    Result addGraduate(GraduateAddDTO dto, Integer operatorUserId, Integer operatorRole);

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

    /**
     * 修改毕业生信息
     * - 学生角色：只能修改自己的毕业生信息
     * - 教师/管理员角色：可以修改其管辖范围内的毕业生信息
     * @param dto 修改毕业生信息请求参数
     * @param operatorUserId 操作用户ID
     * @param operatorRole 操作用户角色
     */
    Result updateGraduate(GraduateUpdateDTO dto, Integer operatorUserId, Integer operatorRole);
}
