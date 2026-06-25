package com.marler.alumnus.controller;

import com.marler.alumnus.dto.StudentUpdateDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.StudentService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/student/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Student student) {
        try {
            log.info("添加学生请求 - userId: {}, studentNo: {}",
                    student.getUserId(), student.getStudentNo());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加学生");

            Result result = studentService.add(student);
            log.info("添加学生结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("添加学生失败", e);
            return Result.error("添加学生失败：" + e.getMessage());
        }
    }

    @PostMapping("/student/update")
    public Result update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody StudentUpdateDTO studentUpdateDTO) {
        try {
            log.info("修改学生信息请求 - studentId: {}", studentUpdateDTO.getId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            if (user.getRole() == 1) {
                Student existingStudent = studentService.findStudentById(studentUpdateDTO.getId());
                if (existingStudent == null) return Result.error("学生记录不存在");
                if (!existingStudent.getUserId().equals(tokenUserId)) {
                    return Result.error("无权修改其他学生的信息");
                }
            }

            Result result = studentService.update(studentUpdateDTO);
            log.info("修改学生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("修改学生信息失败", e);
            return Result.error("修改学生信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/info")
    public Result getStudentInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            log.info("查询学生信息请求 - userId: {}", userId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            Result result = studentService.getStudentInfo(userId, tokenUserId, user.getRole());
            log.info("查询学生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询学生信息失败", e);
            return Result.error("查询学生信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/student/list")
    public Result getAllStudents(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有学生列表请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看所有学生信息");

            Result result = studentService.getAllStudents();
            log.info("查询所有学生列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询所有学生列表失败", e);
            return Result.error("查询所有学生列表失败：" + e.getMessage());
        }
    }
}
