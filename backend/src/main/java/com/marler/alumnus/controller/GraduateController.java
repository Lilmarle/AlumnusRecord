package com.marler.alumnus.controller;

import com.marler.alumnus.dto.GraduateAddDTO;
import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.dto.GraduateUpdateDTO;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.GraduateService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class GraduateController {

    @Autowired
    private GraduateService graduateService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/graduate/submit")
    public Result submit(
            @RequestHeader("Authorization") String authorization,
            @RequestBody GraduateSubmitDTO dto) {
        try {
            log.info("提交毕业生信息请求 - studentId: {}", dto.getStudentId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() != 1) return Result.error("仅学生角色可以提交毕业生信息");

            Result result = graduateService.submit(dto, tokenUserId);
            log.info("提交毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("提交毕业生信息失败", e);
            return Result.error("提交毕业生信息失败：" + e.getMessage());
        }
    }

    /**
     * 教师/管理员添加毕业生
     * - 教师角色：只能添加自己的学生为毕业生（自动确定教师类型）
     * - 管理员角色：可以添加所有学生为毕业生（需指定关联的教师ID和类型）
     * POST /graduate/add
     */
    @PostMapping("/graduate/add")
    public Result addGraduate(
            @RequestHeader("Authorization") String authorization,
            @RequestBody GraduateAddDTO dto) {
        try {
            log.info("添加毕业生请求 - studentId: {}, operatorRole: 教师/管理员", dto.getStudentId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可添加毕业生");

            Result result = graduateService.addGraduate(dto, tokenUserId, user.getRole());
            log.info("添加毕业生结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("添加毕业生失败", e);
            return Result.error("添加毕业生失败：" + e.getMessage());
        }
    }

    @GetMapping("/graduate/info")
    public Result getInfo(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询毕业生信息请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            Result result = graduateService.getByUserId(tokenUserId);
            log.info("查询毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询毕业生信息失败", e);
            return Result.error("查询毕业生信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/graduate/list")
    public Result getAllGraduates(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有毕业生信息请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看毕业生信息");

            Result result = graduateService.getAllGraduates();
            log.info("查询所有毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询所有毕业生信息失败", e);
            return Result.error("查询所有毕业生信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/graduate/statistics")
    public Result getStatistics(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询毕业生去向统计请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看毕业生去向统计");

            Result result = graduateService.getDestinationStatistics();
            log.info("查询毕业生去向统计结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询毕业生去向统计失败", e);
            return Result.error("查询毕业生去向统计失败：" + e.getMessage());
        }
    }

    /**
     * 修改毕业生信息
     * - 学生角色：只能修改自己的毕业生信息
     * - 教师角色：可以修改自己指导/管理的毕业生信息
     * - 管理员角色：可以修改所有毕业生信息
     * PUT /graduate/update
     */
    @PutMapping("/graduate/update")
    public Result updateGraduate(
            @RequestHeader("Authorization") String authorization,
            @RequestBody GraduateUpdateDTO dto) {
        try {
            log.info("修改毕业生信息请求 - graduateId: {}", dto.getId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            Result result = graduateService.updateGraduate(dto, tokenUserId, user.getRole());
            log.info("修改毕业生信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("修改毕业生信息失败", e);
            return Result.error("修改毕业生信息失败：" + e.getMessage());
        }
    }
}
