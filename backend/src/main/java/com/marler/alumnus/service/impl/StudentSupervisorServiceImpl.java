package com.marler.alumnus.service.impl;

import com.marler.alumnus.mapper.StudentMapper;
import com.marler.alumnus.mapper.StudentSupervisorMapper;
import com.marler.alumnus.mapper.SupervisorMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.pojo.StudentSupervisor;
import com.marler.alumnus.pojo.Supervisor;
import com.marler.alumnus.service.StudentSupervisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class StudentSupervisorServiceImpl implements StudentSupervisorService {

    private static final Logger log = LoggerFactory.getLogger(StudentSupervisorServiceImpl.class);

    @Autowired
    private StudentSupervisorMapper studentSupervisorMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional
    public Result add(StudentSupervisor studentSupervisor) {
        log.debug("开始添加导师-学生关联 - supId: {}, stuId: {}",
                studentSupervisor.getSupId(), studentSupervisor.getStuId());

        if (studentSupervisor.getSupId() == null) {
            return Result.error("导师记录ID不能为空");
        }
        if (studentSupervisor.getStuId() == null) {
            return Result.error("学生记录ID不能为空");
        }

        Supervisor supervisor = supervisorMapper.findById(studentSupervisor.getSupId());
        if (supervisor == null) {
            return Result.error("导师记录不存在");
        }

        Student student = studentMapper.findById(studentSupervisor.getStuId());
        if (student == null) {
            return Result.error("学生记录不存在");
        }

        StudentSupervisor existing = studentSupervisorMapper.findActiveRelation(
                studentSupervisor.getSupId(), studentSupervisor.getStuId());
        if (existing != null) {
            return Result.error("该导师已关联该学生，不能重复添加");
        }

        if (studentSupervisor.getIsPrimary() == null) {
            studentSupervisor.setIsPrimary(true);
        }

        try {
            int rows = studentSupervisorMapper.insert(studentSupervisor);
            if (rows <= 0) {
                return Result.error("添加关联失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加关联失败 - 数据库异常: {}", e.getMessage());
            return Result.error("添加关联失败，请稍后重试");
        }

        log.info("添加导师-学生关联成功 - id: {}, supId: {}, stuId: {}",
                studentSupervisor.getId(), studentSupervisor.getSupId(), studentSupervisor.getStuId());
        return Result.success("导师-学生关联添加成功", studentSupervisor);
    }

    @Override
    public Result getStudentsBySupervisor(Integer supId) {
        if (supId == null) return Result.error("导师记录ID不能为空");
        List<Map<String, Object>> list = studentSupervisorMapper.findStudentsBySupId(supId);
        return Result.success(list);
    }

    @Override
    public Result getSupervisorsByStudent(Integer stuId) {
        if (stuId == null) return Result.error("学生记录ID不能为空");
        List<Map<String, Object>> list = studentSupervisorMapper.findSupervisorsByStuId(stuId);
        return Result.success(list);
    }

    @Override
    public Result getAllRelations() {
        List<Map<String, Object>> list = studentSupervisorMapper.findAllRelations();
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result endRelation(Integer id, String endDate) {
        if (id == null) return Result.error("关联记录ID不能为空");
        StudentSupervisor relation = studentSupervisorMapper.findById(id);
        if (relation == null) return Result.error("关联记录不存在");

        StudentSupervisor updateObj = new StudentSupervisor();
        updateObj.setId(id);
        updateObj.setEndDate(endDate);
        updateObj.setIsPrimary(false);

        int rows = studentSupervisorMapper.update(updateObj);
        if (rows <= 0) return Result.error("结束指导失败，请稍后重试");

        return Result.success("已结束该导师对学生的指导");
    }

    @Override
    @Transactional
    public Result deleteRelation(Integer id) {
        if (id == null) return Result.error("关联记录ID不能为空");
        StudentSupervisor relation = studentSupervisorMapper.findById(id);
        if (relation == null) return Result.error("关联记录不存在");

        int rows = studentSupervisorMapper.deleteById(id);
        if (rows <= 0) return Result.error("删除关联失败，请稍后重试");

        return Result.success("导师-学生关联已删除");
    }
}
