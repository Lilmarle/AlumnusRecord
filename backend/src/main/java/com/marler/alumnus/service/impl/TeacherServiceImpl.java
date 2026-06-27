package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.TeacherUpdateDTO;
import com.marler.alumnus.dto.VTeacherInfo;
import com.marler.alumnus.mapper.TeacherMapper;
import com.marler.alumnus.mapper.UserProfileMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.pojo.UserProfile;
import com.marler.alumnus.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public Result update(TeacherUpdateDTO dto) {
        log.debug("开始修改教师信息 - teacherId: {}", dto.getId());

        // 1. 参数校验
        if (dto.getId() == null) {
            log.warn("修改教师信息失败 - 教师记录ID为空");
            return Result.error("教师记录ID不能为空");
        }

        // 2. 查询教师记录是否存在
        Teacher existingTeacher = teacherMapper.findById(dto.getId());
        if (existingTeacher == null) {
            log.warn("修改教师信息失败 - 教师记录不存在: id={}", dto.getId());
            return Result.error("教师记录不存在");
        }

        // 3. 更新 teacher 表
        Teacher teacher = new Teacher();
        teacher.setId(dto.getId());
        teacher.setCollegeId(dto.getCollegeId());
        teacher.setTitle(dto.getTitle());
        teacher.setEmployeeNo(dto.getEmployeeNo());
        teacher.setHireDate(dto.getHireDate());

        int rows = teacherMapper.update(teacher);
        if (rows <= 0) {
            log.error("修改教师信息失败 - 数据库更新异常: id={}", dto.getId());
            return Result.error("修改教师信息失败，请稍后重试");
        }

        // 4. 如果同时提供了个人信息字段，则更新 user_profile 表
        boolean hasProfileFields = dto.getName() != null
                || dto.getGender() != null
                || dto.getAvatar() != null
                || dto.getIdCard() != null;

        if (hasProfileFields) {
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(existingTeacher.getUserId());
            userProfile.setName(dto.getName());
            userProfile.setGender(dto.getGender());
            userProfile.setAvatar(dto.getAvatar());
            userProfile.setIdCard(dto.getIdCard());

            int profileRows = userProfileMapper.updateUserProfile(userProfile);
            if (profileRows <= 0) {
                log.warn("修改教师信息 - 用户档案更新可能未生效: userId={}", existingTeacher.getUserId());
            }
        }

        // 5. 重新查询更新后的教师信息
        Teacher updatedTeacher = teacherMapper.findById(dto.getId());

        // 6. 构造详细返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("teacherId", dto.getId());
        data.put("userId", existingTeacher.getUserId());
        if (updatedTeacher != null) {
            data.put("collegeId", updatedTeacher.getCollegeId());
            data.put("title", updatedTeacher.getTitle());
            data.put("employeeNo", updatedTeacher.getEmployeeNo());
            data.put("hireDate", updatedTeacher.getHireDate());
            data.put("updateTime", updatedTeacher.getUpdateTime());
        }

        // 记录哪些字段被更新了
        Map<String, Object> changes = new HashMap<>();
        boolean hasChanges = false;
        if (dto.getCollegeId() != null) { changes.put("collegeId", dto.getCollegeId()); hasChanges = true; }
        if (dto.getTitle() != null) { changes.put("title", dto.getTitle()); hasChanges = true; }
        if (dto.getEmployeeNo() != null) { changes.put("employeeNo", dto.getEmployeeNo()); hasChanges = true; }
        if (dto.getHireDate() != null) { changes.put("hireDate", dto.getHireDate()); hasChanges = true; }
        if (dto.getName() != null) { changes.put("name", dto.getName()); hasChanges = true; }
        if (dto.getGender() != null) { changes.put("gender", dto.getGender()); hasChanges = true; }
        if (dto.getIdCard() != null) { changes.put("idCard", dto.getIdCard()); hasChanges = true; }
        data.put("updatedFields", hasChanges ? changes : "无字段更新");

        log.info("修改教师信息成功 - teacherId: {}, userId: {}, 更新字段: {}", dto.getId(), existingTeacher.getUserId(), changes.keySet());
        return Result.success("教师信息修改成功", data);
    }

    @Override
    public Teacher findTeacherById(Integer id) {
        return teacherMapper.findById(id);
    }

    @Override
    public Result getTeacherInfo(Integer userId, Integer tokenUserId, Integer role) {
        log.debug("查询教师信息 - userId: {}, tokenUserId: {}, role: {}", userId, tokenUserId, role);

        // 1. 参数校验
        if (tokenUserId == null || role == null) {
            log.warn("查询教师信息失败 - 用户身份信息缺失");
            return Result.error("用户身份信息缺失");
        }

        // 2. 权限校验：教师(role=2)只能查询自己的信息，管理员(role=3)可查任意
        Integer targetUserId = userId;
        if (role == 2) {
            // 教师角色，强制查询自己的信息
            if (userId != null && !userId.equals(tokenUserId)) {
                log.warn("查询教师信息失败 - 教师角色无权查看他人信息: tokenUserId={}, targetUserId={}", tokenUserId, userId);
                return Result.error("无权查看其他教师的信息");
            }
            targetUserId = tokenUserId;
        }

        // 3. 如果没有指定目标用户ID
        if (targetUserId == null) {
            log.warn("查询教师信息失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 4. 查询教师信息（关联学院、档案）
        VTeacherInfo teacherInfo = teacherMapper.findTeacherInfoByUserId(targetUserId);
        if (teacherInfo == null) {
            log.warn("查询教师信息失败 - 未找到该教师的信息: userId={}", targetUserId);
            return Result.error("未找到该教师的信息");
        }

        log.info("查询教师信息成功 - userId: {}, name: {}", targetUserId, teacherInfo.getRealName());
        return Result.success(teacherInfo);
    }

    @Override
    public Result getAllTeachers() {
        log.debug("查询所有教师列表");

        List<VTeacherInfo> list = teacherMapper.findAllTeacherInfo();

        log.info("查询所有教师列表成功 - 共 {} 条记录", list.size());
        return Result.success(list);
    }
}
