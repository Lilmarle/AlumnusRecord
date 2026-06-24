package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.CounselorStudent;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.CounselorStudentService;
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
@RequestMapping("/counselor-student")
public class CounselorStudentController {

    private static final Logger log = LoggerFactory.getLogger(CounselorStudentController.class);

    @Autowired
    private CounselorStudentService counselorStudentService;

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
     * 添加辅导员-学生关联
     * POST /counselor-student/add
     * 权限：仅管理员(role=3)可用
     */
    @PostMapping("/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CounselorStudent counselorStudent) {
        log.info("添加辅导员-学生关联请求 - couId: {}, stuId: {}",
                counselorStudent.getCouId(), counselorStudent.getStuId());

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

        // 3. 权限校验：仅管理员(role=3)可添加关联
        if (user.getRole() < 3) {
            log.warn("添加关联失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可添加辅导员-学生关联");
        }

        // 4. 调用 service
        Result result = counselorStudentService.add(counselorStudent);
        log.info("添加辅导员-学生关联结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 根据辅导员记录ID查询其管理的学生列表
     * GET /counselor-student/list-by-counselor?couId=xxx
     * 权限：教师(role=2)和管理员(role=3)可用
     */
    @GetMapping("/list-by-counselor")
    public Result getStudentsByCounselor(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("couId") Integer couId) {
        log.info("查询辅导员管理的学生列表请求 - couId: {}", couId);

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        if (user.getRole() < 2) {
            log.warn("查询失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，无法查看");
        }

        return counselorStudentService.getStudentsByCounselor(couId);
    }

    /**
     * 根据学生记录ID查询其所属的辅导员列表
     * GET /counselor-student/list-by-student?stuId=xxx
     * 权限：所有已登录用户可用
     */
    @GetMapping("/list-by-student")
    public Result getCounselorsByStudent(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("stuId") Integer stuId) {
        log.info("查询学生的辅导员列表请求 - stuId: {}", stuId);

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        return counselorStudentService.getCounselorsByStudent(stuId);
    }

    /**
     * 查询所有辅导员-学生关联记录
     * GET /counselor-student/list
     * 权限：教师(role=2)和管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllRelations(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有辅导员-学生关联记录请求");

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        if (user.getRole() < 2) {
            log.warn("查询失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，无法查看");
        }

        return counselorStudentService.getAllRelations();
    }

    /**
     * 结束辅导员对学生的管理
     * POST /counselor-student/end
     * 权限：管理员(role=3)可用
     */
    @PostMapping("/end")
    public Result endRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> params) {
        Integer id = params.get("id") != null ? Integer.valueOf(params.get("id").toString()) : null;
        String endDate = params.get("endDate") != null ? params.get("endDate").toString() : null;
        log.info("结束辅导员管理学生请求 - relationId: {}, endDate: {}", id, endDate);

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        if (user.getRole() < 3) {
            log.warn("结束管理失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可操作");
        }

        return counselorStudentService.endRelation(id, endDate);
    }

    /**
     * 删除辅导员-学生关联记录
     * DELETE /counselor-student/delete
     * 权限：仅管理员(role=3)可用
     */
    @DeleteMapping("/delete")
    public Result deleteRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("id") Integer id) {
        log.info("删除辅导员-学生关联请求 - relationId: {}", id);

        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        User user = userMapper.findById(tokenUserId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }

        if (user.getRole() < 3) {
            log.warn("删除关联失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可操作");
        }

        return counselorStudentService.deleteRelation(id);
    }
}
