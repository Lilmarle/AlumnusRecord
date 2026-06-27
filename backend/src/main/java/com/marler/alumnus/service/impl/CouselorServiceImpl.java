package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.VCouselorInfo;
import com.marler.alumnus.mapper.CouselorMapper;
import com.marler.alumnus.mapper.TeacherMapper;
import com.marler.alumnus.pojo.Couselor;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.service.CouselorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouselorServiceImpl implements CouselorService {

    private static final Logger log = LoggerFactory.getLogger(CouselorServiceImpl.class);

    @Autowired
    private CouselorMapper couselorMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    @Transactional
    public Result add(Couselor couselor) {
        log.debug("开始添加辅导员 - teacherId: {}", couselor.getTeacherId());

        // 1. 参数校验
        if (couselor.getTeacherId() == null) {
            log.warn("添加辅导员失败 - 教师ID为空");
            return Result.error("教师ID不能为空");
        }
        if (couselor.getUserId() == null) {
            log.warn("添加辅导员失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 2. 检查教师记录是否存在
        Teacher teacher = teacherMapper.findById(couselor.getTeacherId());
        if (teacher == null) {
            log.warn("添加辅导员失败 - 教师记录不存在: teacherId={}", couselor.getTeacherId());
            return Result.error("教师记录不存在");
        }

        // 3. 设置默认值
        if (couselor.getIsActive() == null) {
            couselor.setIsActive(true);
        }

        // 4. 执行插入
        try {
            int rows = couselorMapper.insert(couselor);
            if (rows <= 0) {
                log.error("添加辅导员失败 - 数据库插入异常");
                return Result.error("添加辅导员失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加辅导员失败 - 数据库异常: {}", e.getMessage());
            // 处理唯一索引冲突（同一教师只能有一条辅导员记录）
            if (e.getMessage() != null && e.getMessage().contains("uk_teacher_id")) {
                return Result.error("该教师已是辅导员，不能重复添加");
            }
            return Result.error("添加辅导员失败，请稍后重试");
        }

        log.info("添加辅导员成功 - id: {}, teacherId: {}, userId: {}",
                couselor.getId(), couselor.getTeacherId(), couselor.getUserId());
        return Result.success("辅导员添加成功", couselor);
    }

    @Override
    public Result getCounselorInfo(Integer userId, Integer tokenUserId, Integer role) {
        log.debug("查询辅导员信息 - userId: {}, tokenUserId: {}, role: {}", userId, tokenUserId, role);

        // 1. 参数校验
        if (tokenUserId == null || role == null) {
            log.warn("查询辅导员信息失败 - 用户身份信息缺失");
            return Result.error("用户身份信息缺失");
        }

        // 2. 权限校验：教师(role=2)只能查询自己的辅导员信息，管理员(role=3)可查任意
        Integer targetUserId = userId;
        if (role == 2) {
            // 教师角色，强制查询自己的信息
            if (userId != null && !userId.equals(tokenUserId)) {
                log.warn("查询辅导员信息失败 - 教师角色无权查看他人信息: tokenUserId={}, targetUserId={}", tokenUserId, userId);
                return Result.error("无权查看其他教师的辅导员信息");
            }
            targetUserId = tokenUserId;
        }

        // 3. 如果没有指定目标用户ID
        if (targetUserId == null) {
            log.warn("查询辅导员信息失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 4. 查询辅导员完整信息
        VCouselorInfo info = couselorMapper.findCounselorInfoByUserId(targetUserId);
        if (info == null) {
            log.warn("查询辅导员信息失败 - 未找到该教师的辅导员信息: userId={}", targetUserId);
            return Result.error("未找到该教师的辅导员信息");
        }

        log.info("查询辅导员信息成功 - userId: {}, realName: {}", targetUserId, info.getRealName());
        return Result.success(info);
    }

    @Override
    public Result getAllCounselors() {
        log.debug("查询所有辅导员列表");

        List<VCouselorInfo> list = couselorMapper.findAllCounselorInfo();

        log.info("查询所有辅导员列表成功 - 共 {} 条记录", list.size());
        return Result.success(list);
    }
}
