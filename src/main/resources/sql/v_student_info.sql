-- ============================================
-- 创建学生信息视图 v_student_info
-- 包含学生基本信息、学院/专业/班级、用户状态、
-- 主要辅导员姓名、主导师（学业导师）姓名
-- ============================================

CREATE OR REPLACE VIEW `v_student_info` AS
SELECT
    s.id AS student_id,
    s.user_id,
    s.student_no,
    u.phone,
    u.status AS user_status,
    CASE u.status
        WHEN 1 THEN '在校'
        WHEN 2 THEN '离校'
        ELSE '未知'
        END AS status_name,
    p.name AS real_name,
    p.gender,
    CASE p.gender
        WHEN 1 THEN '男'
        WHEN 2 THEN '女'
        ELSE '未知'
        END AS gender_name,
    p.avatar,
    p.id_card,
    c.college_name,
    m.major_name,
    cl.class_name,
    cl.grade,
    s.enroll_date,
    s.graduate_date,
    -- 主要辅导员姓名
    u_counselor.name AS counselor_name,
    -- 主导师（学业导师）姓名
    u_supervisor.name AS supervisor_name,
    s.create_time,
    s.update_time
FROM student s
         INNER JOIN user u ON s.user_id = u.id
         LEFT JOIN user_profile p ON s.user_id = p.user_id
         INNER JOIN college c ON s.college_id = c.id
         INNER JOIN major m ON s.major_id = m.id
         INNER JOIN class cl ON s.class_id = cl.id
-- 关联主要辅导员（is_primary = 1 且 end_date IS NULL）
         LEFT JOIN counselor_student cs ON s.id = cs.stu_id AND cs.is_primary = 1 AND cs.end_date IS NULL
         LEFT JOIN counselor counselor ON cs.cou_id = counselor.id
         LEFT JOIN user_profile u_counselor ON counselor.user_id = u_counselor.user_id
-- 关联主导师（is_primary = 1 且 supervisor_type = 1 学业导师 且 end_date IS NULL）
         LEFT JOIN student_supervisor ss ON s.id = ss.stu_id AND ss.is_primary = 1 AND ss.end_date IS NULL
         LEFT JOIN supervisor supervisor ON ss.sup_id = supervisor.id AND supervisor.supervisor_type = 1
         LEFT JOIN user_profile u_supervisor ON supervisor.user_id = u_supervisor.user_id;
