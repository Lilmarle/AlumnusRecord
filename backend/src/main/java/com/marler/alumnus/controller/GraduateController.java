package com.marler.alumnus.controller;

import com.marler.alumnus.dto.GraduateSubmitDTO;
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
}
