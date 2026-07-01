package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.GraduateAddDTO;
import com.marler.alumnus.dto.GraduateSubmitDTO;
import com.marler.alumnus.dto.GraduateUpdateDTO;
import com.marler.alumnus.dto.VGraduateInfo;
import com.marler.alumnus.dto.VStudentInfo;
import com.marler.alumnus.mapper.GraduateMapper;
import com.marler.alumnus.mapper.StudentMapper;
import com.marler.alumnus.mapper.TeacherGraduateMapper;
import com.marler.alumnus.mapper.TeacherMapper;
import com.marler.alumnus.mapper.TeacherStudentMapper;
import com.marler.alumnus.pojo.Graduate;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.pojo.Teacher;
import com.marler.alumnus.pojo.TeacherGraduate;
import com.marler.alumnus.pojo.TeacherStudent;
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

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private TeacherStudentMapper teacherStudentMapper;

    @Autowired
    private TeacherGraduateMapper teacherGraduateMapper;

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

    @Override
    @Transactional
    public Result addGraduate(GraduateAddDTO dto, Integer operatorUserId, Integer operatorRole) {
        log.debug("教师/管理员添加毕业生 - operatorUserId: {}, role: {}, studentId: {}",
                operatorUserId, operatorRole, dto.getStudentId());

        // 1. 参数校验
        if (dto.getStudentId() == null) {
            log.warn("添加毕业生失败 - 学生记录ID为空");
            return Result.error("学生记录ID不能为空");
        }
        if (dto.getGraduateYear() == null) {
            log.warn("添加毕业生失败 - 毕业年份为空");
            return Result.error("毕业年份不能为空");
        }
        if (dto.getDestination() == null) {
            log.warn("添加毕业生失败 - 去向为空");
            return Result.error("去向不能为空");
        }

        // 2. 查询学生记录是否存在
        Student student = studentMapper.findById(dto.getStudentId());
        if (student == null) {
            log.warn("添加毕业生失败 - 学生记录不存在: studentId={}", dto.getStudentId());
            return Result.error("学生记录不存在");
        }

        // 3. 检查是否已存在毕业生记录
        Graduate existing = graduateMapper.findByStudentId(dto.getStudentId());
        if (existing != null) {
            log.warn("添加毕业生失败 - 该学生已是毕业生: studentId={}", dto.getStudentId());
            return Result.error("该学生已是毕业生，请勿重复添加");
        }

        // 4. 根据角色处理权限和关联信息
        Integer teacherId;
        Integer teacherType;

        if (operatorRole == 2) {
            // 教师角色：只能添加自己的学生
            Teacher teacher = teacherMapper.findByUserId(operatorUserId);
            if (teacher == null) {
                log.warn("添加毕业生失败 - 未找到教师记录: userId={}", operatorUserId);
                return Result.error("未找到该用户的教师记录");
            }

            // 检查该学生是否是该教师的学生
            TeacherStudent relation = teacherStudentMapper.findByStudentIdAndTeacherId(
                    dto.getStudentId(), teacher.getId());
            if (relation == null) {
                log.warn("添加毕业生失败 - 该学生不是当前教师的学生: studentId={}, teacherId={}",
                        dto.getStudentId(), teacher.getId());
                return Result.error("只能添加自己指导/管理的学生为毕业生");
            }

            teacherId = teacher.getId();
            teacherType = relation.getType();

        } else if (operatorRole == 3) {
            // 管理员角色：可以添加所有学生
            if (dto.getTeacherId() == null) {
                log.warn("添加毕业生失败 - 管理员必须指定教师记录ID");
                return Result.error("管理员添加毕业生时，必须指定关联的教师记录ID");
            }
            if (dto.getTeacherType() == null) {
                log.warn("添加毕业生失败 - 管理员必须指定教师类型");
                return Result.error("管理员添加毕业生时，必须指定教师类型（1-辅导员，2-导师）");
            }

            // 验证教师记录存在
            Teacher teacher = teacherMapper.findById(dto.getTeacherId());
            if (teacher == null) {
                log.warn("添加毕业生失败 - 教师记录不存在: teacherId={}", dto.getTeacherId());
                return Result.error("关联的教师记录不存在");
            }

            teacherId = dto.getTeacherId();
            teacherType = dto.getTeacherType();

        } else {
            log.warn("添加毕业生失败 - 无权限: role={}", operatorRole);
            return Result.error("权限不足，仅教师和管理员可添加毕业生");
        }

        // 5. 插入毕业生记录
        Graduate graduate = new Graduate();
        graduate.setStudentId(dto.getStudentId());
        graduate.setUserId(student.getUserId());
        graduate.setGraduateYear(dto.getGraduateYear());
        graduate.setDestination(dto.getDestination());
        graduate.setDestinationDetail(dto.getDestinationDetail());
        graduate.setCertificateNo(dto.getCertificateNo());

        int graduateRows = graduateMapper.insert(graduate);
        if (graduateRows <= 0) {
            log.error("添加毕业生失败 - 数据库插入异常: studentId={}", dto.getStudentId());
            return Result.error("添加毕业生失败，请稍后重试");
        }

        // 6. 插入教师毕业生关联记录
        TeacherGraduate teacherGraduate = new TeacherGraduate();
        teacherGraduate.setTeacherId(teacherId);
        teacherGraduate.setGraduateId(graduate.getId());
        teacherGraduate.setType(teacherType);

        int tgRows = teacherGraduateMapper.insert(teacherGraduate);
        if (tgRows <= 0) {
            log.error("添加毕业生失败 - 教师毕业生关联记录插入异常: graduateId={}, teacherId={}",
                    graduate.getId(), teacherId);
            throw new RuntimeException("添加教师毕业生关联记录失败");
        }

        log.info("教师/管理员添加毕业生成功 - operatorUserId: {}, studentId: {}, graduateId: {}, teacherId: {}, type: {}",
                operatorUserId, dto.getStudentId(), graduate.getId(), teacherId, teacherType);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("graduate", graduate);
        resultData.put("teacherGraduate", teacherGraduate);

        return Result.success("添加毕业生成功", resultData);
    }

    @Override
    @Transactional
    public Result updateGraduate(GraduateUpdateDTO dto, Integer operatorUserId, Integer operatorRole) {
        log.debug("修改毕业生信息 - operatorUserId: {}, role: {}, graduateId: {}",
                operatorUserId, operatorRole, dto.getId());

        // 1. 参数校验
        if (dto.getId() == null) {
            log.warn("修改毕业生信息失败 - 毕业记录ID为空");
            return Result.error("毕业记录ID不能为空");
        }

        // 2. 构造更新对象并执行更新
        Graduate updateObj = new Graduate();
        updateObj.setId(dto.getId());
        updateObj.setGraduateYear(dto.getGraduateYear());
        updateObj.setDestination(dto.getDestination());
        updateObj.setDestinationDetail(dto.getDestinationDetail());
        updateObj.setCertificateNo(dto.getCertificateNo());

        int rows = graduateMapper.updateById(updateObj);
        if (rows <= 0) {
            log.warn("修改毕业生信息失败 - 毕业记录不存在: graduateId={}", dto.getId());
            return Result.error("毕业记录不存在");
        }

        log.info("修改毕业生信息成功 - graduateId: {}, operatorUserId: {}", dto.getId(), operatorUserId);
        return Result.success("修改毕业生信息成功");
    }
}
