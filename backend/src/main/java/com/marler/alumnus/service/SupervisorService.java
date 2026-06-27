package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Supervisor;

public interface SupervisorService {

    /**
     * 添加导师
     */
    Result add(Supervisor supervisor);

    /**
     * 根据用户ID查询导师完整信息
     */
    Result getSupervisorInfo(Integer userId, Integer tokenUserId, Integer role);

    /**
     * 查询所有导师列表
     */
    Result getAllSupervisors();
}
