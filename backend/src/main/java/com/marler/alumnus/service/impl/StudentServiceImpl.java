package com.marler.alumnus.service.impl;

import com.marler.alumnus.dto.StudentUpdateDTO;
import com.marler.alumnus.dto.VStudentInfo;
import com.marler.alumnus.mapper.StudentMapper;
import com.marler.alumnus.mapper.UserMapper;
import com.marler.alumnus.mapper.UserProfileMapper;
import com.marler.alumnus.pojo.Result;
import com.marler.alumnus.pojo.Student;
import com.marler.alumnus.pojo.User;
import com.marler.alumnus.pojo.UserProfile;
import com.marler.alumnus.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result add(Student student) {
        log.debug("开始添加学生 - userId: {}", student.getUserId());

        // 1. 参数校验
        if (student.getUserId() == null) {
            log.warn("添加学生失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }
        if (student.getStudentNo() == null || student.getStudentNo().isEmpty()) {
            log.warn("添加学生失败 - 学号为空");
            return Result.error("学号不能为空");
        }

        // 2. 检查用户是否存在且角色为学生(role=1)
        User user = userMapper.findById(student.getUserId());
        if (user == null) {
            log.warn("添加学生失败 - 用户不存在: userId={}", student.getUserId());
            return Result.error("用户不存在");
        }
        if (user.getRole() != 1) {
            log.warn("添加学生失败 - 用户角色不是学生: userId={}, role={}", student.getUserId(), user.getRole());
            return Result.error("该用户角色不是学生，无法添加为学生记录");
        }

        // 3. 检查是否已存在该用户的学生记录
        Student existingStudent = studentMapper.findByUserId(student.getUserId());
        if (existingStudent != null) {
            log.warn("添加学生失败 - 该用户已有学生记录: userId={}", student.getUserId());
            return Result.error("该用户已是学生，不能重复添加");
        }

        // 4. 执行插入
        try {
            int rows = studentMapper.insert(student);
            if (rows <= 0) {
                log.error("添加学生失败 - 数据库插入异常");
                return Result.error("添加学生失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("添加学生失败 - 数据库异常: {}", e.getMessage());
            return Result.error("添加学生失败，请稍后重试");
        }

        log.info("添加学生成功 - id: {}, userId: {}, studentNo: {}",
                student.getId(), student.getUserId(), student.getStudentNo());
        return Result.success("学生添加成功", student);
    }

    @Override
    @Transactional
    public Result update(StudentUpdateDTO dto) {
        log.debug("开始修改学生信息 - studentId: {}", dto.getId());

        // 1. 参数校验
        if (dto.getId() == null) {
            log.warn("修改学生信息失败 - 学生记录ID为空");
            return Result.error("学生记录ID不能为空");
        }

        // 2. 查询学生记录是否存在
        Student existingStudent = studentMapper.findById(dto.getId());
        if (existingStudent == null) {
            log.warn("修改学生信息失败 - 学生记录不存在: id={}", dto.getId());
            return Result.error("学生记录不存在");
        }

        // 3. 更新 student 表
        Student student = new Student();
        student.setId(dto.getId());
        student.setStudentNo(dto.getStudentNo());
        student.setCollegeId(dto.getCollegeId());
        student.setMajorId(dto.getMajorId());
        student.setClassId(dto.getClassId());
        student.setEnrollDate(dto.getEnrollDate());
        student.setGraduateDate(dto.getGraduateDate());

        int rows = studentMapper.update(student);
        if (rows <= 0) {
            log.error("修改学生信息失败 - 数据库更新异常: id={}", dto.getId());
            return Result.error("修改学生信息失败，请稍后重试");
        }

        // 4. 如果同时提供了个人信息字段，则更新 user_profile 表
        boolean hasProfileFields = dto.getName() != null
                || dto.getGender() != null
                || dto.getAvatar() != null
                || dto.getIdCard() != null;

        if (hasProfileFields) {
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(existingStudent.getUserId());
            userProfile.setName(dto.getName());
            userProfile.setGender(dto.getGender());
            userProfile.setAvatar(dto.getAvatar());
            userProfile.setIdCard(dto.getIdCard());

            int profileRows = userProfileMapper.updateUserProfile(userProfile);
            if (profileRows <= 0) {
                log.warn("修改学生信息 - 用户档案更新可能未生效: userId={}", existingStudent.getUserId());
            }
        }

        // 5. 重新查询更新后的完整学生信息（关联视图）
        VStudentInfo updatedInfo = studentMapper.findStudentInfoByUserId(existingStudent.getUserId());

        // 6. 构造详细返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", dto.getId());
        data.put("userId", existingStudent.getUserId());
        if (updatedInfo != null) {
            data.put("name", updatedInfo.getName());
            data.put("studentNo", updatedInfo.getStudentNo());
            data.put("gender", updatedInfo.getGender());
            data.put("collegeName", updatedInfo.getCollegeName());
            data.put("majorName", updatedInfo.getMajorName());
            data.put("className", updatedInfo.getClassName());
            data.put("grade", updatedInfo.getGrade());
            data.put("enrollDate", updatedInfo.getEnrollDate());
            data.put("graduateDate", updatedInfo.getGraduateDate());
        }

        // 记录哪些字段被更新了
        Map<String, Object> changes = new HashMap<>();
        boolean hasChanges = false;
        if (dto.getStudentNo() != null) { changes.put("studentNo", dto.getStudentNo()); hasChanges = true; }
        if (dto.getCollegeId() != null) { changes.put("collegeId", dto.getCollegeId()); hasChanges = true; }
        if (dto.getMajorId() != null) { changes.put("majorId", dto.getMajorId()); hasChanges = true; }
        if (dto.getClassId() != null) { changes.put("classId", dto.getClassId()); hasChanges = true; }
        if (dto.getEnrollDate() != null) { changes.put("enrollDate", dto.getEnrollDate()); hasChanges = true; }
        if (dto.getGraduateDate() != null) { changes.put("graduateDate", dto.getGraduateDate()); hasChanges = true; }
        if (dto.getName() != null) { changes.put("name", dto.getName()); hasChanges = true; }
        if (dto.getGender() != null) { changes.put("gender", dto.getGender()); hasChanges = true; }
        if (dto.getIdCard() != null) { changes.put("idCard", dto.getIdCard()); hasChanges = true; }
        data.put("updatedFields", hasChanges ? changes : "无字段更新");

        log.info("修改学生信息成功 - studentId: {}, userId: {}, 更新字段: {}", dto.getId(), existingStudent.getUserId(), changes.keySet());
        return Result.success("学生信息修改成功", data);
    }

    @Override
    public Student findStudentById(Integer id) {
        return studentMapper.findById(id);
    }

    @Override
    public Result getStudentInfo(Integer userId, Integer tokenUserId, Integer role) {
        log.debug("查询学生信息 - userId: {}, tokenUserId: {}, role: {}", userId, tokenUserId, role);

        // 1. 参数校验
        if (tokenUserId == null || role == null) {
            log.warn("查询学生信息失败 - 用户身份信息缺失");
            return Result.error("用户身份信息缺失");
        }

        // 2. 权限校验：学生只能查询自己的信息
        Integer targetUserId = userId;
        if (role == 1) {
            // 学生角色，强制查询自己的信息
            if (userId != null && !userId.equals(tokenUserId)) {
                log.warn("查询学生信息失败 - 学生角色无权查看他人信息: tokenUserId={}, targetUserId={}", tokenUserId, userId);
                return Result.error("无权查看其他学生的信息");
            }
            targetUserId = tokenUserId;
        }

        // 3. 如果没有指定目标用户ID，且是教师/管理员，返回错误提示
        if (targetUserId == null) {
            log.warn("查询学生信息失败 - 用户ID为空");
            return Result.error("用户ID不能为空");
        }

        // 4. 查询 v_student_info 视图获取完整信息
        VStudentInfo studentInfo = studentMapper.findStudentInfoByUserId(targetUserId);
        if (studentInfo == null) {
            log.warn("查询学生信息失败 - 未找到该学生的信息: userId={}", targetUserId);
            return Result.error("未找到该学生的信息");
        }

        log.info("查询学生信息成功 - userId: {}, name: {}", targetUserId, studentInfo.getName());
        return Result.success(studentInfo);
    }

    @Override
    public Result getAllStudents() {
        log.debug("查询所有学生列表");

        List<VStudentInfo> list = studentMapper.findAllStudentInfo();

        log.info("查询所有学生列表成功 - 共 {} 条记录", list.size());
        return Result.success(list);
    }
}
