package com.marler.alumnus.service;

import com.marler.alumnus.dto.ChangePasswordDTO;
import com.marler.alumnus.dto.RegisterDTO;
import com.marler.alumnus.pojo.Result;

public interface UserService {

    /**
     * 用户登录（支持用户名或手机号 + 密码）
     */
    Result login(String account, String password);

    /**
     * 用户注册
     */
    Result register(RegisterDTO registerDTO);

    /**
     * 修改密码
     */
    Result changePassword(ChangePasswordDTO changePasswordDTO);

    /**
     * 注销登录（将 Token 加入 Redis 黑名单）
     */
    Result logout(String token);
}
