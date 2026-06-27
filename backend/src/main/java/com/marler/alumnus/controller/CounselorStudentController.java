package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.pojo.CounselorStudent;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.service.CounselorStudentService;
import com.marler.alumnus.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class CounselorStudentController {

    @Autowired
    private CounselorStudentService counselorStudentService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/counselor-student/add")
    public Result add(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CounselorStudent counselorStudent) {
        try {
            log.info("添加辅导员-学生关联请求 - couId: {}, stuId: {}",
                    counselorStudent.getCouId(), counselorStudent.getStuId());

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可添加辅导员-学生关联");

            Result result = counselorStudentService.add(counselorStudent);
            log.info("添加辅导员-学生关联结果 - code: {}, message: {}", result.getCode(), result.getMessage());
            return result;
        } catch (Exception e) {
            log.error("添加辅导员-学生关联失败", e);
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @GetMapping("/counselor-student/list-by-counselor")
    public Result getStudentsByCounselor(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("couId") Integer couId) {
        try {
            log.info("查询辅导员管理的学生列表请求 - couId: {}", couId);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，无法查看");

            return counselorStudentService.getStudentsByCounselor(couId);
        } catch (Exception e) {
            log.error("查询辅导员管理的学生列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/counselor-student/list-by-student")
    public Result getCounselorsByStudent(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("stuId") Integer stuId) {
        try {
            log.info("查询学生的辅导员列表请求 - stuId: {}", stuId);

            JwtUtils.parseToken(authorization.replace("Bearer ", ""));

            return counselorStudentService.getCounselorsByStudent(stuId);
        } catch (Exception e) {
            log.error("查询学生的辅导员列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/counselor-student/list")
    public Result getAllRelations(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("查询所有辅导员-学生关联记录请求");

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 2) return Result.error("权限不足，无法查看");

            return counselorStudentService.getAllRelations();
        } catch (Exception e) {
            log.error("查询所有关联记录失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @PostMapping("/counselor-student/end")
    public Result endRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> params) {
        try {
            Integer id = params.get("id") != null ? Integer.valueOf(params.get("id").toString()) : null;
            String endDate = params.get("endDate") != null ? params.get("endDate").toString() : null;
            log.info("结束辅导员管理学生请求 - relationId: {}, endDate: {}", id, endDate);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

            return counselorStudentService.endRelation(id, endDate);
        } catch (Exception e) {
            log.error("结束辅导员管理学生失败", e);
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/counselor-student/delete")
    public Result deleteRelation(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("id") Integer id) {
        try {
            log.info("删除辅导员-学生关联请求 - relationId: {}", id);

            Claims claims = JwtUtils.parseToken(authorization.replace("Bearer ", ""));
            Integer tokenUserId = Integer.valueOf(claims.getSubject());

            User user = userMapper.findById(tokenUserId);
            if (user == null) return Result.error("用户不存在");
            if (user.getRole() < 3) return Result.error("权限不足，仅管理员可操作");

            return counselorStudentService.deleteRelation(id);
        } catch (Exception e) {
            log.error("删除辅导员-学生关联失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
