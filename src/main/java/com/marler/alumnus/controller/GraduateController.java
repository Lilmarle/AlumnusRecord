package com.marler.alumnus.controller;

import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.GraduateService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/graduate")
public class GraduateController {

    private static final Logger log = LoggerFactory.getLogger(GraduateController.class);

    @Autowired
    private GraduateService graduateService;

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
     * 提交毕业生信息接口
     * POST /graduate/submit
     * 仅学生角色(role=1)可提交，且需为4年级（大四）
     */
    @PostMapping("/submit")
    public Result submit(
            @RequestHeader("Authorization") String authorization,
            @RequestBody GraduateSubmitDTO dto) {
        log.info("提交毕业生信息请求 - studentId: {}", dto.getStudentId());

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

        // 权限校验：仅学生(role=1)可提交毕业生信息
        if (user.getRole() != 1) {
            log.warn("提交毕业生信息失败 - 非学生角色无法提交: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("仅学生角色可以提交毕业生信息");
        }

        Result result = graduateService.submit(dto, tokenUserId);
        log.info("提交毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询自己的毕业生信息接口
     * GET /graduate/info
     */
    @GetMapping("/info")
    public Result getInfo(@RequestHeader("Authorization") String authorization) {
        log.info("查询毕业生信息请求");

        // 解析 Token 获取当前用户信息
        Integer tokenUserId = getUserIdFromToken(authorization);
        if (tokenUserId == null) {
            return Result.unauthorized("未登录或 Token 无效");
        }

        Result result = graduateService.getByUserId(tokenUserId);
        log.info("查询毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询所有毕业生信息接口
     * GET /graduate/list
     * 教师(role=2)和管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllGraduates(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有毕业生信息请求");

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

        // 权限校验：教师(2)和管理员(3)可查看
        if (user.getRole() < 2) {
            log.warn("查询所有毕业生信息失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅教师和管理员可查看毕业生信息");
        }

        Result result = graduateService.getAllGraduates();
        log.info("查询所有毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 获取毕业生去向统计接口
     * GET /graduate/statistics
     * 教师(role=2)和管理员(role=3)可用
     * 返回按去向、年份、学院分组统计数据
     */
    @GetMapping("/statistics")
    public Result getStatistics(@RequestHeader("Authorization") String authorization) {
        log.info("查询毕业生去向统计请求");

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

        // 权限校验：教师(2)和管理员(3)可查看
        if (user.getRole() < 2) {
            log.warn("查询毕业生去向统计失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅教师和管理员可查看毕业生去向统计");
        }

        Result result = graduateService.getDestinationStatistics();
        log.info("查询毕业生去向统计结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
