package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.TeacherStudentService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TeacherStudentController {

    @Autowired
    private TeacherStudentService teacherStudentService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/teacher-student/list")
    public Result getStudentsByTeacher(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("teacherId") Integer teacherId,
            @RequestParam("type") Integer type) {
        try {
            log.info("查询教师关联的学生列表请求 - teacherId: {}, type: {}", teacherId, type);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足");

            return teacherStudentService.getStudentsByTeacher(teacherId, type);
        } catch (Exception e) {
            log.error("查询教师关联的学生列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
