package com.marler.alumnus.service.impl;

import com.marler.alumnus.mapper.TeacherStudentMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.service.TeacherStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TeacherStudentServiceImpl implements TeacherStudentService {

    private static final Logger log = LoggerFactory.getLogger(TeacherStudentServiceImpl.class);

    @Autowired
    private TeacherStudentMapper teacherStudentMapper;

    @Override
    public Result getStudentsByTeacher(Integer teacherId) {
        log.debug("查询教师关联的学生列表 - teacherId: {}", teacherId);

        if (teacherId == null) {
            log.warn("查询失败 - 教师ID为空");
            return Result.error("教师ID不能为空");
        }

        List<Map<String, Object>> list = teacherStudentMapper.findStudentsByTeacherId(teacherId);
        log.info("查询教师关联的学生列表成功 - teacherId: {}, 共 {} 条", teacherId, list.size());
        return Result.success(list);
    }
}
