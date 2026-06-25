package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.Couselor;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.CouselorService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class CouselorController {

    @Autowired
    private CouselorService couselorService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/couselor/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Couselor couselor) {
        try {
            log.info("添加辅导员请求 - teacherId: {}, userId: {}",
                    couselor.getTeacherId(), couselor.getUserId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加辅导员");

            Result result = couselorService.add(couselor);
            log.info("添加辅导员结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("添加辅导员失败", e);
            return Result.error("添加辅导员失败：" + e.getMessage());
        }
    }

    @GetMapping("/couselor/info")
    public Result getCounselorInfo(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            log.info("查询辅导员信息请求 - userId: {}", userId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");

            Result result = couselorService.getCounselorInfo(userId, tokenUserId, user.getRole());
            log.info("查询辅导员信息结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询辅导员信息失败", e);
            return Result.error("查询辅导员信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/couselor/list")
    public Result getAllCounselors(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有辅导员列表请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，仅教师和管理员可查看所有辅导员信息");

            Result result = couselorService.getAllCounselors();
            log.info("查询所有辅导员列表结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("查询所有辅导员列表失败", e);
            return Result.error("查询所有辅导员列表失败：" + e.getMessage());
        }
    }
}
