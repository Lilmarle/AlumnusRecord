package com.marler.alumnus.controller;

import com.marler.alumnus.dto.LoginDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口（支持用户名/手机号 + 密码）
     * POST /user/login
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO.getAccount(), loginDTO.getPassword());
    }
}
