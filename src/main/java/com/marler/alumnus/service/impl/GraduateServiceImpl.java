package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.dto.VGraduateInfo;
import com.marler.alumnus.dto.VStudentInfo;
import com.marler.alumnus.mapper.GraduateMapper;
import com.marler.alumnus.mapper.StudentMapper;
import com.marler.alumnus.pojo.Graduate;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.service.GraduateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraduateServiceImpl implements GraduateService {

    private static final Logger log = LoggerFactory.getLogger(GraduateServiceImpl.class);

    @Autowired
    private GraduateMapper graduateMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional
    public Result submit(GraduateSubmitDTO dto, Integer userId) {
        log.debug("提交毕业生信息 - userId: {}, studentId: {}", userId, dto.getStudentId());

        // 1. 参数校验
        if (dto.getStudentId() == null) {
            log.warn("提交毕业生信息失败 - 学生记录ID为空");
            return Result.error("学生记录ID不能为空");
        }
        if (dto.getGraduateYear() == null) {
            log.warn("提交毕业生信息失败 - 毕业年份为空");
            return Result.error("毕业年份不能为空");
        }
        if (dto.getDestination() == null) {
            log.warn("提交毕业生信息失败 - 去向为空");
            return Result.error("去向不能为空");
        }

        // 2. 查询学生记录是否存在
        Student student = studentMapper.findById(dto.getStudentId());
        if (student == null) {
            log.warn("提交毕业生信息失败 - 学生记录不存在: studentId={}", dto.getStudentId());
            return Result.error("学生记录不存在");
        }

        // 3. 校验是否为该学生本人操作
        if (!student.getUserId().equals(userId)) {
            log.warn("提交毕业生信息失败 - 无权操作他人信息: userId={}, studentUserId={}", userId, student.getUserId());
            return Result.error("只能提交自己的毕业生信息");
        }

        // 4. 查询学生完整信息（含年级），检查是否为4年级（大四）
        VStudentInfo studentInfo = studentMapper.findStudentInfoByUserId(userId);
        if (studentInfo == null) {
            log.warn("提交毕业生信息失败 - 未找到学生完整信息: userId={}", userId);
            return Result.error("未找到学生信息");
        }

        String grade = studentInfo.getGrade();
        log.debug("学生年级: {}", grade);

        // 检查年级是否为4（大四）
        if (grade == null || !"4".equals(grade.trim())) {
            log.warn("提交毕业生信息失败 - 非大四学生不能提交毕业生信息: userId={}, grade={}", userId, grade);
            return Result.error("仅大四年级学生可以提交毕业生信息，当前年级为: " + (grade == null ? "未设置" : grade));
        }

        // 5. 检查是否已提交过毕业生信息
        Graduate existing = graduateMapper.findByUserId(userId);
        if (existing != null) {
            log.warn("提交毕业生信息失败 - 已提交过毕业生信息: userId={}", userId);
            return Result.error("您已提交过毕业生信息，请勿重复提交");
        }

        // 6. 插入毕业生记录
        Graduate graduate = new Graduate();
        graduate.setStudentId(dto.getStudentId());
        graduate.setUserId(userId);
        graduate.setGraduateYear(dto.getGraduateYear());
        graduate.setDestination(dto.getDestination());
        graduate.setDestinationDetail(dto.getDestinationDetail());
        graduate.setCertificateNo(dto.getCertificateNo());

        int rows = graduateMapper.insert(graduate);
        if (rows <= 0) {
            log.error("提交毕业生信息失败 - 数据库插入异常: userId={}", userId);
            return Result.error("提交毕业生信息失败，请稍后重试");
        }

        log.info("提交毕业生信息成功 - userId: {}, studentId: {}, year: {}, destination: {}",
                userId, dto.getStudentId(), dto.getGraduateYear(), dto.getDestination());
        return Result.success("毕业生信息提交成功", graduate);
    }

    @Override
    public Result getByUserId(Integer userId) {
        log.debug("查询毕业生信息 - userId: {}", userId);

        if (userId == null) {
            log.warn("查询毕业生信息失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        Graduate graduate = graduateMapper.findByUserId(userId);
        if (graduate == null) {
            log.warn("查询毕业生信息失败 - 未找到毕业生记录: userId={}", userId);
            return Result.error("未找到毕业生记录");
        }

        log.info("查询毕业生信息成功 - userId: {}", userId);
        return Result.success(graduate);
    }

    @Override
    public Result getAllGraduates() {
        log.debug("查询所有毕业生信息");

        List<VGraduateInfo> list = graduateMapper.findAll();

        log.info("查询所有毕业生信息成功 - 共 {} 条记录", list.size());
        return Result.success(list);
    }

    @Override
    public Result getDestinationStatistics() {
        log.debug("查询毕业生去向统计");

        // 1. 按去向分组统计
        List<Map<String, Object>> byDestination = graduateMapper.countByDestination();

        // 2. 按年份和去向分组统计
        List<Map<String, Object>> byYearAndDestination = graduateMapper.countByYearAndDestination();

        // 3. 按学院和去向分组统计
        List<Map<String, Object>> byCollegeAndDestination = graduateMapper.countByCollegeAndDestination();

        // 4. 毕业生总数
        Long totalCount = graduateMapper.totalCount();

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("byDestination", byDestination);
        result.put("byYearAndDestination", byYearAndDestination);
        result.put("byCollegeAndDestination", byCollegeAndDestination);

        log.info("查询毕业生去向统计成功 - 去向分布: {}, 年份分布: {}, 学院分布: {}",
                byDestination.size(), byYearAndDestination.size(), byCollegeAndDestination.size());

        return Result.success(result);
    }
}
