package com.marler.alumnus.service.impl;

import com.marler.alumnus.mapper.CounselorStudentMapper;
import com.marler.alumnus.mapper.CouselorMapper;
import com.marler.alumnus.mapper.StudentMapper;
import com.marler.alumnus.pojo.CounselorStudent;
import com.marler.alumnus.pojo.Couselor;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.service.CounselorStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CounselorStudentServiceImpl implements CounselorStudentService {

    private static final Logger log = LoggerFactory.getLogger(CounselorStudentServiceImpl.class);

    @Autowired
    private CounselorStudentMapper counselorStudentMapper;

    @Autowired
    private CouselorMapper couselorMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional
    public Result add(CounselorStudent counselorStudent) {
        log.debug("开始添加辅导员-学生关联 - couId: {}, stuId: {}", 
                counselorStudent.getCouId(), counselorStudent.getStuId());

        // 1. 参数校验
        if (counselorStudent.getCouId() == null) {
            log.warn("添加关联失败 - 辅导员记录ID为空");
            return Result.error("辅导员记录ID不能为空");
        }
        if (counselorStudent.getStuId() == null) {
            log.warn("添加关联失败 - 学生记录ID为空");
            return Result.error("学生记录ID不能为空");
        }

        // 2. 检查辅导员记录是否存在
        Couselor couselor = couselorMapper.findById(counselorStudent.getCouId());
        if (couselor == null) {
            log.warn("添加关联失败 - 辅导员记录不存在: couId={}", counselorStudent.getCouId());
            return Result.error("辅导员记录不存在");
        }

        // 3. 检查学生记录是否存在
        Student student = studentMapper.findById(counselorStudent.getStuId());
        if (student == null) {
            log.warn("添加关联失败 - 学生记录不存在: stuId={}", counselorStudent.getStuId());
            return Result.error("学生记录不存在");
        }

        // 4. 检查是否已存在有效关联
        CounselorStudent existing = counselorStudentMapper.findActiveRelation(
                counselorStudent.getCouId(), counselorStudent.getStuId());
        if (existing != null) {
            log.warn("添加关联失败 - 该辅导员已关联该学生: couId={}, stuId={}", 
                    counselorStudent.getCouId(), counselorStudent.getStuId());
            return Result.error("该辅导员已关联该学生，不能重复添加");
        }

        // 5. 设置默认值
        if (counselorStudent.getIsPrimary() == null) {
            counselorStudent.setIsPrimary(true);
        }

        // 6. 执行插入
        try {
            int rows = counselorStudentMapper.insert(counselorStudent);
            if (rows <= 0) {
                log.error("添加关联失败 - 数据库插入异常");
                return Result.error("添加关联失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加关联失败 - 数据库异常: {}", e.getMessage());
            return Result.error("添加关联失败，请稍后重试");
        }

        log.info("添加辅导员-学生关联成功 - id: {}, couId: {}, stuId: {}",
                counselorStudent.getId(), counselorStudent.getCouId(), counselorStudent.getStuId());
        return Result.success("辅导员-学生关联添加成功", counselorStudent);
    }

    @Override
    public Result getStudentsByCounselor(Integer couId) {
        log.debug("查询辅导员管理的学生列表 - couId: {}", couId);

        if (couId == null) {
            log.warn("查询失败 - 辅导员记录ID为空");
            return Result.error("辅导员记录ID不能为空");
        }

        List<Map<String, Object>> list = counselorStudentMapper.findStudentsByCouId(couId);
        log.info("查询辅导员管理的学生列表成功 - couId: {}, 共 {} 条", couId, list.size());
        return Result.success(list);
    }

    @Override
    public Result getCounselorsByStudent(Integer stuId) {
        log.debug("查询学生的辅导员列表 - stuId: {}", stuId);

        if (stuId == null) {
            log.warn("查询失败 - 学生记录ID为空");
            return Result.error("学生记录ID不能为空");
        }

        List<Map<String, Object>> list = counselorStudentMapper.findCounselorsByStuId(stuId);
        log.info("查询学生的辅导员列表成功 - stuId: {}, 共 {} 条", stuId, list.size());
        return Result.success(list);
    }

    @Override
    public Result getAllRelations() {
        log.debug("查询所有辅导员-学生关联记录");

        List<Map<String, Object>> list = counselorStudentMapper.findAllRelations();
        log.info("查询所有辅导员-学生关联记录成功 - 共 {} 条", list.size());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result endRelation(Integer id, String endDate) {
        log.debug("结束辅导员管理学生 - relationId: {}, endDate: {}", id, endDate);

        if (id == null) {
            log.warn("结束管理失败 - 关联记录ID为空");
            return Result.error("关联记录ID不能为空");
        }

        CounselorStudent relation = counselorStudentMapper.findById(id);
        if (relation == null) {
            log.warn("结束管理失败 - 关联记录不存在: id={}", id);
            return Result.error("关联记录不存在");
        }

        CounselorStudent updateObj = new CounselorStudent();
        updateObj.setId(id);
        updateObj.setEndDate(endDate);
        updateObj.setIsPrimary(false);

        int rows = counselorStudentMapper.update(updateObj);
        if (rows <= 0) {
            log.error("结束管理失败 - 数据库更新异常: id={}", id);
            return Result.error("结束管理失败，请稍后重试");
        }

        log.info("结束辅导员管理学生成功 - relationId: {}, endDate: {}", id, endDate);
        return Result.success("已结束该辅导员对学生的管理");
    }

    @Override
    @Transactional
    public Result deleteRelation(Integer id) {
        log.debug("删除辅导员-学生关联 - relationId: {}", id);

        if (id == null) {
            log.warn("删除关联失败 - 关联记录ID为空");
            return Result.error("关联记录ID不能为空");
        }

        CounselorStudent relation = counselorStudentMapper.findById(id);
        if (relation == null) {
            log.warn("删除关联失败 - 关联记录不存在: id={}", id);
            return Result.error("关联记录不存在");
        }

        int rows = counselorStudentMapper.deleteById(id);
        if (rows <= 0) {
            log.error("删除关联失败 - 数据库删除异常: id={}", id);
            return Result.error("删除关联失败，请稍后重试");
        }

        log.info("删除辅导员-学生关联成功 - relationId: {}", id);
        return Result.success("辅导员-学生关联已删除");
    }
}
