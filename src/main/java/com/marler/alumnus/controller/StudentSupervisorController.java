package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.StudentSupervisor;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.StudentSupervisorService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/student-supervisor")
public class StudentSupervisorController {

    private static final Logger log = LoggerFactory.getLogger(StudentSupervisorController.class);

    @Autowired
    private StudentSupervisorService studentSupervisorService;

    @Autowired
    private UserMapper userMapper;

    private Integer getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
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
     * 添加导师-学生关联
     * POST /student-supervisor/add
     * 权限：仅管理员(role=3)
     */
    @PostMapping("/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody StudentSupervisor studentSupervisor) {
        log.info("添加导师-学生关联请求 - supId: {}, stuId: {}",
                studentSupervisor.getSupId(), studentSupervisor.getStuId());

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");

        User user = userMapper.findById(tokenUserId);
        if (user == null) return Result.unauthorized("用户不存在");
        if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加导师-学生关联");

        return studentSupervisorService.add(studentSupervisor);
    }

    /**
     * 根据导师记录ID查询其指导的学生列表
     * GET /student-supervisor/list-by-supervisor?supId=xxx
     */
    @GetMapping("/list-by-supervisor")
    public Result getStudentsBySupervisor(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("supId") Integer supId) {
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");
        User user = userMapper.findById(tokenUserId);
        if (user == null) return Result.unauthorized("用户不存在");
        if (user.getRole() < 2) return Result.error("权限不足");
        return studentSupervisorService.getStudentsBySupervisor(supId);
    }

    /**
     * 根据学生记录ID查询其所属的导师列表
     * GET /student-supervisor/list-by-student?stuId=xxx
     */
    @GetMapping("/list-by-student")
    public Result getSupervisorsByStudent(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("stuId") Integer stuId) {
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");
        return studentSupervisorService.getSupervisorsByStudent(stuId);
    }

    /**
     * 查询所有导师-学生关联记录
     * GET /student-supervisor/list
     */
    @GetMapping("/list")
    public Result getAllRelations(@RequestHeader("Authorization") String authorization) {
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");
        User user = userMapper.findById(tokenUserId);
        if (user == null) return Result.unauthorized("用户不存在");
        if (user.getRole() < 2) return Result.error("权限不足");
        return studentSupervisorService.getAllRelations();
    }

    /**
     * 结束导师对学生的指导
     * POST /student-supervisor/end
     */
    @PostMapping("/end")
    public Result endRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> params) {
        Integer id = params.get("id") != null ? Integer.valueOf(params.get("id").toString()) : null;
        String endDate = params.get("endDate") != null ? params.get("endDate").toString() : null;

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");
        User user = userMapper.findById(tokenUserId);
        if (user == null) return Result.unauthorized("用户不存在");
        if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

        return studentSupervisorService.endRelation(id, endDate);
    }

    /**
     * 删除导师-学生关联记录
     * DELETE /student-supervisor/delete
     */
    @DeleteMapping("/delete")
    public Result deleteRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("id") Integer id) {
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) return Result.unauthorized("未登录或 Token 无效");
        User user = userMapper.findById(tokenUserId);
        if (user == null) return Result.unauthorized("用户不存在");
        if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

        return studentSupervisorService.deleteRelation(id);
    }
}
