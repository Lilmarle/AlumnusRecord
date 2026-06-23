package com.marler.alumnus.controller;

import com.marler.alumnus.dto.TeacherUpdateDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.TeacherService;
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
@RequestMapping("/teacher")
public class TeacherController {

    private static final Logger log = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherService teacherService;

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
     * 修改教师信息接口
     * POST /teacher/update
     * 支持同时修改 teacher 表字段和 user_profile 表字段（姓名、性别、头像、身份证号）
     * 权限：教师(role=2)只能修改自己的信息，管理员(role=3)可修改任意教师的信息
     */
    @PostMapping("/update")
    public Result update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TeacherUpdateDTO teacherUpdateDTO) {
        log.info("修改教师信息请求 - teacherId: {}", teacherUpdateDTO.getId());

        // 解析 Token 获取当前用户信息
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        // 查询用户角色
        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 权限校验：教师(role=2)只能修改自己的信息，管理员(role=3)可修改任意
        if (user.getRole() == 2) {
            Teacher existingTeacher = teacherService.findTeacherById(teacherUpdateDTO.getId());
            if (existingTeacher == null) {
                return Result.error("教师记录不存在");
            }
            if (!existingTeacher.getUserId().equals(tokenUserId)) {
                log.warn("修改教师信息失败 - 教师角色无权修改他人信息: tokenUserId={}, teacherUserId={}",
                        tokenUserId, existingTeacher.getUserId());
                return Result.error("无权修改其他教师的信息");
            }
        } else if (user.getRole() < 2) {
            log.warn("修改教师信息失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，无法修改教师信息");
        }

        Result result = teacherService.update(teacherUpdateDTO);
        log.info("修改教师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询教师信息接口
     * GET /teacher/info?userId=xxx
     * 根据用户ID查询完整的教师信息（包含学院名称、档案）
     * 权限：教师(role=2)只能查自己，管理员(role=3)可查任意
     */
    @GetMapping("/info")
    public Result getTeacherInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        log.info("查询教师信息请求 - userId: {}", userId);

        // 解析 Token 获取当前用户信息
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        // 查询用户角色
        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        Result result = teacherService.getTeacherInfo(userId, tokenUserId, user.getRole());
        log.info("查询教师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询所有教师列表接口
     * GET /teacher/list
     * 仅管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllTeachers(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有教师列表请求");

        // 解析 Token 获取当前用户信息
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        // 查询用户角色
        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 权限校验：教师(2)和管理员(3)均可查看
        if (user.getRole() < 2) {
            log.warn("查询所有教师列表失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，无法查看教师信息");
        }

        Result result = teacherService.getAllTeachers();
        log.info("查询所有教师列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
