package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.StudentSupervisor;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.StudentSupervisorService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class StudentSupervisorController {

    @Autowired
    private StudentSupervisorService studentSupervisorService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/student-supervisor/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody StudentSupervisor studentSupervisor) {
        try {
            log.info("添加导师-学生关联请求 - supId: {}, stuId: {}",
                    studentSupervisor.getSupId(), studentSupervisor.getStuId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加导师-学生关联");

            return studentSupervisorService.add(studentSupervisor);
        } catch (Exception e) {
            log.error("添加导师-学生关联失败", e);
            return Result.error("添加导师-学生关联失败：" + e.getMessage());
        }
    }

    @GetMapping("/student-supervisor/list-by-supervisor")
    public Result getStudentsBySupervisor(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("supId") Integer supId) {
        try {
            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足");

            return studentSupervisorService.getStudentsBySupervisor(supId);
        } catch (Exception e) {
            log.error("查询导师指导的学生列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/student-supervisor/list-by-student")
    public Result getSupervisorsByStudent(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("stuId") Integer stuId) {
        try {
            JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            return studentSupervisorService.getSupervisorsByStudent(stuId);
        } catch (Exception e) {
            log.error("查询学生的导师列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/student-supervisor/list")
    public Result getAllRelations(@RequestHeader("Authorization") String authorization) {
        try {
            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足");

            return studentSupervisorService.getAllRelations();
        } catch (Exception e) {
            log.error("查询所有关联记录失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @PostMapping("/student-supervisor/end")
    public Result endRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> params) {
        try {
            Integer id = params.get("id") != null ? Integer.valueOf(params.get("id").toString()) : null;
            String endDate = params.get("endDate") != null ? params.get("endDate").toString() : null;

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

            return studentSupervisorService.endRelation(id, endDate);
        } catch (Exception e) {
            log.error("结束导师指导失败", e);
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/student-supervisor/delete")
    public Result deleteRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("id") Integer id) {
        try {
            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

            return studentSupervisorService.deleteRelation(id);
        } catch (Exception e) {
            log.error("删除导师-学生关联失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
