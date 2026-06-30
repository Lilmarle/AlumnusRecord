package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.TeacherGraduateService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TeacherGraduateController {

    @Autowired
    private TeacherGraduateService teacherGraduateService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询当前登录教师所指导/管理的毕业生信息
     * GET /teacher/graduates
     */
    @GetMapping("/teacher/graduates")
    public Result getMyGraduates(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询当前教师用户的毕业生信息请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() != 2) return Result.error("权限不足，仅教师角色可查看");

            Result result = teacherGraduateService.getMyGraduates(tokenUserId);
            log.info("查询当前教师用户的毕业生信息结果 - code: {}, message: {}",
                    result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询教师关联的毕业生信息失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据教师记录ID查询该教师指导/管理的毕业生信息（管理员使用）
     * GET /teacher/graduates/{teacherId}
     */
    @GetMapping("/teacher/graduates/{teacherId}")
    public Result getGraduatesByTeacherId(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer teacherId) {
        try {
            log.info("查询教师关联的毕业生信息请求 - teacherId: {}", teacherId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看");

            Result result = teacherGraduateService.getGraduatesByTeacherId(teacherId);
            log.info("查询教师关联的毕业生信息结果 - code: {}, message: {}",
                    result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询教师关联的毕业生信息失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
