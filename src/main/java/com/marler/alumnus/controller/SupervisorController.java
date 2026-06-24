package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Supervisor;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.SupervisorService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supervisor")
public class SupervisorController {

    private static final Logger log = LoggerFactory.getLogger(SupervisorController.class);

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 从 Authorization 头中提取并解析 Token，获取用户ID
     */
    private Integer getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authorization.substring(7);
            Claims claims = JwtUtils.parseToken(token);
            return Integer.valueOf(claims.getSubject());
        } catch (Exception e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 添加导师接口
     * POST /supervisor/add
     * 权限：仅管理员(role=3)可用
     */
    @PostMapping("/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Supervisor supervisor) {
        log.info("添加导师请求 - teacherId: {}, userId: {}, type: {}",
                supervisor.getTeacherId(), supervisor.getUserId(), supervisor.getSupervisorType());

        // 1. 解析 Token
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        // 2. 查询用户角色
        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 3. 权限校验：仅管理员(role=3)可添加导师
        if (user.getRole() < 3) {
            log.warn("添加导师失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可添加导师");
        }

        // 4. 调用 service
        Result result = supervisorService.add(supervisor);
        log.info("添加导师结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询导师信息接口
     * GET /supervisor/info?userId=xxx
     * 权限：教师(role=2)查自己，管理员(role=3)查任意
     */
    @GetMapping("/info")
    public Result getSupervisorInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        log.info("查询导师信息请求 - userId: {}", userId);

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        Result result = supervisorService.getSupervisorInfo(userId, tokenUserId, user.getRole());
        log.info("查询导师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询所有导师列表接口
     * GET /supervisor/list
     * 权限：教师(role=2)和管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllSupervisors(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有导师列表请求");

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 权限校验：教师(role=2)和管理员(role=3)
        if (user.getRole() < 2) {
            log.warn("查询所有导师列表失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅教师和管理员可查看所有导师信息");
        }

        Result result = supervisorService.getAllSupervisors();
        log.info("查询所有导师列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
