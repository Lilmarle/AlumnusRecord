package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.VGraduateInfo;
import com.marler.alumnus.mapper.TeacherGraduateMapper;
import com.marler.alumnus.mapper.TeacherMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.service.TeacherGraduateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TeacherGraduateServiceImpl implements TeacherGraduateService {

    @Autowired
    private TeacherGraduateMapper teacherGraduateMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Result getGraduatesByTeacherId(Integer teacherId) {
        log.debug("查询教师关联的毕业生信息 - teacherId: {}", teacherId);

        if (teacherId == null) {
            log.warn("查询教师关联的毕业生失败 - 教师记录ID为空");
            return Result.error("教师记录ID不能为空");
        }

        List<VGraduateInfo> list = teacherGraduateMapper.findGraduatesByTeacherId(teacherId);

        log.info("查询教师关联的毕业生信息成功 - teacherId: {}, 共 {} 条记录", teacherId, list.size());
        return Result.success(list);
    }

    @Override
    public Result getMyGraduates(Integer userId) {
        log.debug("查询当前教师用户的毕业生信息 - userId: {}", userId);

        if (userId == null) {
            log.warn("查询当前教师用户的毕业生失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 根据用户ID查询教师记录
        Teacher teacher = teacherMapper.findByUserId(userId);
        if (teacher == null) {
            log.warn("查询当前教师用户的毕业生失败 - 未找到教师记录: userId={}", userId);
            return Result.error("未找到该用户的教师记录");
        }

        List<VGraduateInfo> list = teacherGraduateMapper.findGraduatesByTeacherId(teacher.getId());

        log.info("查询当前教师用户的毕业生信息成功 - userId: {}, teacherId: {}, 共 {} 条记录",
                userId, teacher.getId(), list.size());
        return Result.success(list);
    }
}
