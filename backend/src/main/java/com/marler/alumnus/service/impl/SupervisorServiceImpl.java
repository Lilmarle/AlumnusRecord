package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.VSupervisorInfo;
import com.marler.alumnus.mapper.SupervisorMapper;
import com.marler.alumnus.mapper.TeacherMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Supervisor;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.service.SupervisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupervisorServiceImpl implements SupervisorService {

    private static final Logger log = LoggerFactory.getLogger(SupervisorServiceImpl.class);

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    @Transactional
    public Result add(Supervisor supervisor) {
        log.debug("开始添加导师 - teacherId: {}, supervisorType: {}", supervisor.getTeacherId(), supervisor.getSupervisorType());

        // 1. 参数校验
        if (supervisor.getTeacherId() == null) {
            log.warn("添加导师失败 - 教师ID为空");
            return Result.error("教师ID不能为空");
        }
        if (supervisor.getUserId() == null) {
            log.warn("添加导师失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }
        if (supervisor.getSupervisorType() == null) {
            log.warn("添加导师失败 - 导师类型为空");
            return Result.error("导师类型不能为空");
        }

        // 2. 检查教师记录是否存在
        Teacher teacher = teacherMapper.findById(supervisor.getTeacherId());
        if (teacher == null) {
            log.warn("添加导师失败 - 教师记录不存在: teacherId={}", supervisor.getTeacherId());
            return Result.error("教师记录不存在");
        }

        // 3. 设置默认值
        if (supervisor.getIsActive() == null) {
            supervisor.setIsActive(true);
        }

        // 4. 执行插入
        try {
            int rows = supervisorMapper.insert(supervisor);
            if (rows <= 0) {
                log.error("添加导师失败 - 数据库插入异常");
                return Result.error("添加导师失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加导师失败 - 数据库异常: {}", e.getMessage());
            // 处理唯一索引冲突（同一教师同类型只能有一条记录）
            if (e.getMessage() != null && e.getMessage().contains("uk_teacher_type")) {
                return Result.error("该教师已存在相同类型的导师记录，不能重复添加");
            }
            return Result.error("添加导师失败，请稍后重试");
        }

        log.info("添加导师成功 - id: {}, teacherId: {}, userId: {}, type: {}",
                supervisor.getId(), supervisor.getTeacherId(), supervisor.getUserId(), supervisor.getSupervisorType());
        return Result.success("导师添加成功", supervisor);
    }

    @Override
    public Result getSupervisorInfo(Integer userId, Integer tokenUserId, Integer role) {
        log.debug("查询导师信息 - userId: {}, tokenUserId: {}, role: {}", userId, tokenUserId, role);

        if (tokenUserId == null || role == null) {
            log.warn("查询导师信息失败 - 用户身份信息缺失");
            return Result.error("用户身份信息缺失");
        }

        Integer targetUserId = userId;
        if (role == 2) {
            if (userId != null && !userId.equals(tokenUserId)) {
                log.warn("查询导师信息失败 - 教师角色无权查看他人信息: tokenUserId={}, targetUserId={}", tokenUserId, userId);
                return Result.error("无权查看其他教师的导师信息");
            }
            targetUserId = tokenUserId;
        }

        if (targetUserId == null) {
            log.warn("查询导师信息失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 先查教师记录（只要是教师就能查到）
        Teacher teacher = teacherMapper.findByUserId(targetUserId);
        if (teacher == null) {
            log.warn("查询导师信息失败 - 未找到教师记录: userId={}", targetUserId);
            return Result.error("未找到该教师的记录");
        }

        // 再查导师记录，没有导师记录只是说明没有指导学生
        VSupervisorInfo info = supervisorMapper.findByUserIdFull(targetUserId);
        if (info == null) {
            log.info("该教师尚未被设置为导师，返回教师基本信息 - userId: {}", targetUserId);
            return Result.success(null);
        }

        log.info("查询导师信息成功 - userId: {}, realName: {}", targetUserId, info.getRealName());
        return Result.success(info);
    }

    @Override
    public Result getAllSupervisors() {
        log.debug("查询所有导师列表");

        List<VSupervisorInfo> list = supervisorMapper.findAll();

        log.info("查询所有导师列表成功 - 共 {} 条记录", list.size());
        return Result.success(list);
    }
}
