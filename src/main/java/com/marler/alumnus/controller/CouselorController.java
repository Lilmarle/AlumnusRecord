package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Couselor;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.CouselorService;
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
@RequestMapping("/couselor")
public class CouselorController {

    private static final Logger log = LoggerFactory.getLogger(CouselorController.class);

    @Autowired
    private CouselorService couselorService;

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
     * 添加辅导员接口
     * POST /couselor/add
     * 权限：仅管理员(role=3)可用
     */
    @PostMapping("/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Couselor couselor) {
        log.info("添加辅导员请求 - teacherId: {}, userId: {}",
                couselor.getTeacherId(), couselor.getUserId());

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

        // 3. 权限校验：仅管理员(role=3)可添加辅导员
        if (user.getRole() < 3) {
            log.warn("添加辅导员失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅管理员可添加辅导员");
        }

        // 4. 调用 service 层处理
        Result result = couselorService.add(couselor);
        log.info("添加辅导员结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询辅导员信息接口
     * GET /couselor/info?userId=xxx
     * 权限：教师(role=2)只能查自己的辅导员信息，管理员(role=3)可查任意
     */
    @GetMapping("/info")
    public Result getCounselorInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        log.info("查询辅导员信息请求 - userId: {}", userId);

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

        Result result = couselorService.getCounselorInfo(userId, tokenUserId, user.getRole());
        log.info("查询辅导员信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 查询所有辅导员列表接口
     * GET /couselor/list
     * 仅管理员(role=3)可用
     */
    @GetMapping("/list")
    public Result getAllCounselors(@RequestHeader("Authorization") String authorization) {
        log.info("查询所有辅导员列表请求");

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

        // 权限校验：教师(role=2)和管理员(role=3)可查看所有辅导员列表
        if (user.getRole() < 2) {
            log.warn("查询所有辅导员列表失败 - 权限不足: userId={}, role={}", tokenUserId, user.getRole());
            return Result.error("权限不足，仅教师和管理员可查看所有辅导员信息");
        }

        Result result = couselorService.getAllCounselors();
        log.info("查询所有辅导员列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
        return result;
    }
}
