package com.marler.alumnus.controller;

import com.marler.alumnus.dto.StudentUpdateDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.StudentService;
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
@RequestMapping("/student")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

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
     * 添加学生接口
     * POST /student/add
     * 权限：仅管理员(role=3)可用
     */
    @PostMapping("/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Student student) {
        log.info("添加学生请求 - userId: {}, studentNo: {}",
                student.getUserId(), student.getStudentNo());

        // 1. 解析 Token 获取当前用户信息
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        // 2. 查询用户角色
        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        // 3. 权限校验：仅管理员(role=3)可添加学生
        if (user.getRole() < 3) {
            log.warn("添加学生失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可添加学生");
        }

        // 4. 调用 service 层处理
        Result result = studentService.add(student);
        log.info("添加学生结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 修改学生信息接口
     * POST /student/update
     * 支持同时修改 student 表字段和 user_profile 表字段（姓名、性别、头像、身份证号）
     * 权限：学生(role=1)只能修改自己的信息，教师/管理员(role>=2)可修改任意学生的信息
     */
    @PostMapping("/update")
    public Result update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody StudentUpdateDTO studentUpdateDTO) {
        log.info("修改学生信息请求 - studentId: {}", studentUpdateDTO.getId());

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

        // 权限校验：学生只能修改自己的信息
        if (user.getRole() == 1) {
            Student existingStudent = studentService.findStudentById(studentUpdateDTO.getId());
            if (existingStudent == null) {
                return Result.error("学生记录不存在");
            }
            if (!existingStudent.getUserId().equals(tokenUserId)) {
                log.warn("修改学生信息失败 - 学生角色无权修改他人信息: tokenUserId={}, studentUserId={}",
                        tokenUserId, existingStudent.getUserId());
                return Result.error("无权修改其他学生的信息");
            }
        }

        Result result = studentService.update(studentUpdateDTO);
        log.info("修改学生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询学生信息接口
     * GET /student/info?userId=xxx
     * 根据用户ID查询完整的学生信息（包含学院、专业、班级、档案）
     * 权限：学生(role=1)只能查自己，教师/管理员(role>=2)可查任意
     */
    @GetMapping("/info")
    public Result getStudentInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        log.info("查询学生信息请求 - userId: {}", userId);

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

        Result result = studentService.getStudentInfo(userId, tokenUserId, user.getRole());
        log.info("查询学生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询所有学生列表接口
     * GET /student/list
     * 仅教师(role=2)和管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllStudents(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有学生列表请求");

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

        // 权限校验：仅教师(2)和管理员(3)
        if (user.getRole() < 2) {
            log.warn("查询所有学生列表失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅教师和管理员可查看所有学生信息");
        }

        Result result = studentService.getAllStudents();
        log.info("查询所有学生列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
