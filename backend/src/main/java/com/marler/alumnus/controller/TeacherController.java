package com.marler.alumnus.controller;

import com.marler.alumnus.dto.TeacherUpdateDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.TeacherService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/teacher/update")
    public Result update(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TeacherUpdateDTO teacherUpdateDTO) {
        try {
            log.info("修改教师信息请求 - teacherId: {}", teacherUpdateDTO.getId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            if (user.getRole() == 2) {
                Teacher existingTeacher = teacherService.findTeacherById(teacherUpdateDTO.getId());
                if (existingTeacher == null) return Result.error("教师记录不存在");
                if (!existingTeacher.getUserId().equals(tokenUserId)) {
                    return Result.error("无权修改其他教师的信息");
                }
            } else if (user.getRole() < 2) {
                return Result.error("权限不足，无法修改教师信息");
            }

            Result result = teacherService.update(teacherUpdateDTO);
            log.info("修改教师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("修改教师信息失败", e);
            return Result.error("修改教师信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/teacher/info")
    public Result getTeacherInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            log.info("查询教师信息请求 - userId: {}", userId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            Result result = teacherService.getTeacherInfo(userId, tokenUserId, user.getRole());
            log.info("查询教师信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询教师信息失败", e);
            return Result.error("查询教师信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/teacher/list")
    public Result getAllTeachers(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有教师列表请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，无法查看教师信息");

            Result result = teacherService.getAllTeachers();
            log.info("查询所有教师列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询所有教师列表失败", e);
            return Result.error("查询所有教师列表失败：" + e.getMessage());
        }
    }
}
