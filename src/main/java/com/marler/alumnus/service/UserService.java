package com.marler.alumnus.service;

import com.marler.alumnus.pojo.Result;

public interface UserService {

    /**
     * 用户登录（支持用户名或手机号 + 密码）
     */
    Result login(String account, String password);
}
