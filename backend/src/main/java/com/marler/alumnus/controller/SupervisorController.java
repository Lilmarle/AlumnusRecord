package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Supervisor;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.SupervisorService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class SupervisorController {

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/supervisor/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Supervisor supervisor) {
        try {
            log.info("添加导师请求 - teacherId: {}, userId: {}, type: {}",
                    supervisor.getTeacherId(), supervisor.getUserId(), supervisor.getSupervisorType());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加导师");

            Result result = supervisorService.add(supervisor);
            log.info("添加导师结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("添加导师失败", e);
            return Result.error("添加导师失败：" + e.getMessage());
        }
    }

    @GetMapping("/supervisor/info")
    public Result getSupervisorInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            log.info("查询导师信息请求 - userId: {}", userId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            Result result = supervisorService.getSupervisorInfo(userId, tokenUserId, user.getRole());
            log.info("查询导师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询导师信息失败", e);
            return Result.error("查询导师信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/supervisor/list")
    public Result getAllSupervisors(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有导师列表请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看所有导师信息");

            Result result = supervisorService.getAllSupervisors();
            log.info("查询所有导师列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询所有导师列表失败", e);
            return Result.error("查询所有导师列表失败：" + e.getMessage());
        }
    }
}
