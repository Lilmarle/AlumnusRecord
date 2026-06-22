package com.marler.alumnus.controller;

import com.marler.alumnus.dto.ChangePasswordDTO;
import com.marler.alumnus.dto.LoginDTO;
import com.marler.alumnus.dto.RegisterDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口（支持用户名/手机号 + 密码）
     * POST /user/login
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        log.info("登录请求 - account: {}", loginDTO.getAccount());
        Result result = userService.login(loginDTO.getAccount(), loginDTO.getPassword());
        log.info("登录结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 用户注册接口
     * POST /user/register
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        log.info("注册请求 - username: {}, phone: {}, role: {}",
                registerDTO.getUsername(), registerDTO.getPhone(), registerDTO.getRole());
        Result result = userService.register(registerDTO);
        log.info("注册结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 修改密码接口
     * POST /user/changePassword
     */
    @PostMapping("/changePassword")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("修改密码请求 - userId: {}", changePasswordDTO.getUserId());
        Result result = userService.changePassword(changePasswordDTO);
        log.info("修改密码结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 注销登录接口
     * POST /user/logout
     * 需要从 Authorization 头中获取 Token，将其加入 Redis 黑名单
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authorization) {
        log.info("注销请求");
        // 提取 Bearer Token
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        Result result = userService.logout(token);
        log.info("注销结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
