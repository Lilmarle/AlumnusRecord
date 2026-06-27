package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Couselor;
import com.marler.alumnus.pojo.Result;

public interface CouselorService {

    /**
     * 添加辅导员
     * @param couselor 辅导员信息
     * @return 操作结果
     */
    Result add(Couselor couselor);

    /**
     * 根据用户ID查询辅导员完整信息
     * @param userId 用户ID
     * @param tokenUserId 当前登录用户ID
     * @param role 当前登录用户角色
     * @return 辅导员信息
     */
    Result getCounselorInfo(Integer userId, Integer tokenUserId, Integer role);

    /**
     * 查询所有辅导员列表
     * @return 辅导员列表
     */
    Result getAllCounselors();
}
