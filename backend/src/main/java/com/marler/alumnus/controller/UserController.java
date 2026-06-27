package com.marler.alumnus.controller;

import com.marler.alumnus.dto.ChangePasswordDTO;
import com.marler.alumnus.dto.LoginDTO;
import com.marler.alumnus.dto.RegisterDTO;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        try {
            log.info("登录请求 - account: {}", loginDTO.getAccount());
            Result result = userService.login(loginDTO.getAccount(), loginDTO.getPassword());
            log.info("登录结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/user/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        try {
            log.info("注册请求 - username: {}, phone: {}, role: {}",
                    registerDTO.getUsername(), registerDTO.getPhone(), registerDTO.getRole());
            Result result = userService.register(registerDTO);
            log.info("注册结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    @PostMapping("/user/changePassword")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            log.info("修改密码请求 - userId: {}", changePasswordDTO.getUserId());
            Result result = userService.changePassword(changePasswordDTO);
            log.info("修改密码结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    @PostMapping("/user/logout")
    public Result logout(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("注销请求");
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            Result result = userService.logout(token);
            log.info("注销结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("注销失败", e);
            return Result.error("注销失败：" + e.getMessage());
        }
    }
}
