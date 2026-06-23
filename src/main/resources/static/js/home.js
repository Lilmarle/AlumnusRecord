const { createApp, ref, reactive, computed, onMounted } = Vue;

createApp({
    setup() {
        const loading = ref(false);
        const errorMsg = ref('');

        // ---------- 侧边栏 Tab 相关 ----------
        const activeTab = ref('student');

        const tabList = [
            { key: 'student',  label: '学生管理', icon: '👤' },
            { key: 'teacher',  label: '教师管理', icon: '👨‍🏫' },
            { key: 'graduate', label: '毕业生管理', icon: '🎓' },
            { key: 'counsel',  label: '辅导管理', icon: '💬' },
            { key: 'article',  label: '文章审核', icon: '📝' },
            { key: 'activity', label: '活动审核', icon: '🎪' }
        ];

        function switchTab(key) {
            activeTab.value = key;
        }

        // 用户信息（从 localStorage 获取登录时保存的数据）
        const userInfo = ref(null);

        // 学生详细信息（从后端查询）
        const studentInfo = ref(null);

        // 学生列表（教师/管理员使用）
        const studentList = ref([]);

        // 当前选中的学生（教师/管理员点击查看详情）
        const selectedStudent = ref(null);

        // 是否正在查看详情（教师/管理员使用）
        const viewingDetail = ref(false);

        // ---------- 毕业生管理 ----------
        const graduateList = ref([]);
        const myGraduateInfo = ref(null);
        const searchGraduateKeyword = ref('');
        // 毕业生去向统计
        const destinationStats = ref(null);
        const statsLoading = ref(false);
        // 图表实例
        let pieChartInstance = null;
        let barChartInstance = null;
        const filteredGraduateList = computed(() => {
            const keyword = searchGraduateKeyword.value.trim().toLowerCase();
            if (!keyword) return graduateList.value;
            return graduateList.value.filter(g => {
                return (g.name && g.name.toLowerCase().includes(keyword))
                    || (g.studentNo && g.studentNo.toLowerCase().includes(keyword))
                    || (g.collegeName && g.collegeName.toLowerCase().includes(keyword))
                    || (g.majorName && g.majorName.toLowerCase().includes(keyword))
                    || (g.className && g.className.toLowerCase().includes(keyword))
                    || (g.destinationDetail && g.destinationDetail.toLowerCase().includes(keyword));
            });
        });

        // ---------- 教师管理 ----------
        const teacherInfo = ref(null);
        const teacherList = ref([]);
        const selectedTeacher = ref(null);
        const viewingTeacherDetail = ref(false);
        const searchTeacherKeyword = ref('');

        // 教师编辑状态
        const teacherEditing = ref(false);
        const teacherEditForm = reactive({
            id: null,
            name: '',
            gender: null,
            idCard: '',
            collegeId: null,
            title: '',
            employeeNo: '',
            hireDate: ''
        });
        const teacherSaveLoading = ref(false);
        const teacherSaveSuccess = ref(false);
        const showTeacherModal = ref(false);

        // 搜索关键词（教师/管理员使用）
        const searchKeyword = ref('');

        // 性别映射
        const genderMap = { 0: '未知', 1: '男', 2: '女' };

        // 用户状态映射
        const statusMap = { 1: '在职', 2: '离职' };

        // 毕业生去向映射
        const destinationMap = { 1: '💼 就业', 2: '📋 考公', 3: '📚 考研' };

        // 角色映射
        const roleMap = { 1: '学生', 2: '老师', 3: '管理员' };

        // 获取 Token
        function getToken() {
            return userInfo.value?.token || '';
        }

        // 获取请求头
        function getHeaders() {
            const headers = { 'Content-Type': 'application/json' };
            const token = getToken();
            if (token) {
                headers['Authorization'] = 'Bearer ' + token;
            }
            return headers;
        }

        // 判断是否为教师/管理员
        const isTeacherOrAdmin = computed(() => {
            return userInfo.value && userInfo.value.role >= 2;
        });

        // 判断是否为管理员
        const isAdmin = computed(() => {
            return userInfo.value && userInfo.value.role === 3;
        });

        // 搜索过滤后的学生列表
        const filteredStudentList = computed(() => {
            const keyword = searchKeyword.value.trim().toLowerCase();
            if (!keyword) return studentList.value;
            return studentList.value.filter(s => {
                return (s.name && s.name.toLowerCase().includes(keyword))
                    || (s.studentNo && s.studentNo.toLowerCase().includes(keyword))
                    || (s.collegeName && s.collegeName.toLowerCase().includes(keyword))
                    || (s.majorName && s.majorName.toLowerCase().includes(keyword))
                    || (s.className && s.className.toLowerCase().includes(keyword));
            });
        });

        // 搜索过滤后的教师列表
        const filteredTeacherList = computed(() => {
            const keyword = searchTeacherKeyword.value.trim().toLowerCase();
            if (!keyword) return teacherList.value;
            return teacherList.value.filter(t => {
                return (t.realName && t.realName.toLowerCase().includes(keyword))
                    || (t.employeeNo && t.employeeNo.toLowerCase().includes(keyword))
                    || (t.collegeName && t.collegeName.toLowerCase().includes(keyword))
                    || (t.title && t.title.toLowerCase().includes(keyword));
            });
        });

        // ---------- 初始化：从 localStorage 读取登录信息 ----------
        function loadUserInfo() {
            try {
                const stored = localStorage.getItem('userInfo');
                if (stored) {
                    userInfo.value = JSON.parse(stored);
                } else {
                    // 未登录，跳转到登录页
                    window.location.href = '/index.html';
                }
            } catch (e) {
                console.error('读取用户信息失败', e);
                window.location.href = '/index.html';
            }
        }

        // ---------- 查询学生信息（学生查看自己的信息） ----------
        async function fetchMyStudentInfo() {
            if (!userInfo.value || !userInfo.value.userId) {
                errorMsg.value = '未获取到用户信息，请重新登录';
                return;
            }

            loading.value = true;
            errorMsg.value = '';

            try {
                const response = await fetch(`/student/info?userId=${userInfo.value.userId}`, {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    studentInfo.value = result.data;
                } else if (result.code === 401) {
                    // Token 无效，跳转回登录页
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '查询学生信息失败';
                }
            } catch (err) {
                console.error('查询学生信息异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // ---------- 查询所有学生列表（教师/管理员） ----------
        async function fetchAllStudents() {
            loading.value = true;
            errorMsg.value = '';

            try {
                const response = await fetch('/student/list', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    studentList.value = result.data || [];
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '查询学生列表失败';
                }
            } catch (err) {
                console.error('查询学生列表异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // ---------- 查询自己的教师信息（教师角色查看自己的信息） ----------
        async function fetchMyTeacherInfo() {
            if (!userInfo.value || !userInfo.value.userId) {
                errorMsg.value = '未获取到用户信息，请重新登录';
                return;
            }

            loading.value = true;
            errorMsg.value = '';

            try {
                const response = await fetch(`/teacher/info?userId=${userInfo.value.userId}`, {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    teacherInfo.value = result.data;
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '查询教师信息失败';
                }
            } catch (err) {
                console.error('查询教师信息异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // ---------- 查询所有教师列表（管理员使用） ----------
        async function fetchAllTeachers() {
            loading.value = true;
            errorMsg.value = '';

            try {
                const response = await fetch('/teacher/list', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    teacherList.value = result.data || [];
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '查询教师列表失败';
                }
            } catch (err) {
                console.error('查询教师列表异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // ---------- 查询指定教师的详细信息（管理员点击查看） ----------
        async function viewTeacherDetail(teacher) {
            if (!teacher || !teacher.userId) return;

            loading.value = true;
            errorMsg.value = '';
            selectedTeacher.value = teacher;
            viewingTeacherDetail.value = true;

            try {
                const response = await fetch(`/teacher/info?userId=${teacher.userId}`, {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    teacherInfo.value = result.data;
                } else {
                    errorMsg.value = result.message || '查询教师详情失败';
                }
            } catch (err) {
                console.error('查询教师详情异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // ---------- 教师编辑相关 ----------
        function openTeacherEditModal(teacher) {
            if (!teacher) return;
            teacherEditForm.id = teacher.teacherId;
            teacherEditForm.name = teacher.realName || '';
            teacherEditForm.gender = teacher.gender;
            teacherEditForm.idCard = teacher.idCard || '';
            teacherEditForm.collegeId = teacher.collegeId || null;
            teacherEditForm.title = teacher.title || '';
            teacherEditForm.employeeNo = teacher.employeeNo || '';
            teacherEditForm.hireDate = teacher.hireDate || '';
            teacherSaveSuccess.value = false;
            errorMsg.value = '';
            showTeacherModal.value = true;
        }

        function closeTeacherModal() {
            showTeacherModal.value = false;
            teacherSaveSuccess.value = false;
            errorMsg.value = '';
        }

        function startTeacherEdit() {
            if (!teacherInfo.value) return;
            teacherEditForm.id = teacherInfo.value.teacherId;
            teacherEditForm.name = teacherInfo.value.realName || '';
            teacherEditForm.gender = teacherInfo.value.gender;
            teacherEditForm.idCard = teacherInfo.value.idCard || '';
            teacherEditForm.collegeId = teacherInfo.value.collegeId || null;
            teacherEditForm.title = teacherInfo.value.title || '';
            teacherEditForm.employeeNo = teacherInfo.value.employeeNo || '';
            teacherEditForm.hireDate = teacherInfo.value.hireDate || '';
            teacherEditing.value = true;
            teacherSaveSuccess.value = false;
            errorMsg.value = '';
        }

        function cancelTeacherEdit() {
            teacherEditing.value = false;
            teacherSaveSuccess.value = false;
            errorMsg.value = '';
        }

        async function saveTeacherEdit() {
            errorMsg.value = '';
            teacherSaveSuccess.value = false;

            if (!teacherEditForm.id) {
                errorMsg.value = '教师记录ID缺失';
                return;
            }

            teacherSaveLoading.value = true;
            try {
                const response = await fetch('/teacher/update', {
                    method: 'POST',
                    headers: getHeaders(),
                    body: JSON.stringify({
                        id: teacherEditForm.id,
                        name: teacherEditForm.name || null,
                        gender: teacherEditForm.gender !== null && teacherEditForm.gender !== '' ? Number(teacherEditForm.gender) : null,
                        idCard: teacherEditForm.idCard || null,
                        collegeId: teacherEditForm.collegeId !== null && teacherEditForm.collegeId !== '' ? Number(teacherEditForm.collegeId) : null,
                        title: teacherEditForm.title || null,
                        employeeNo: teacherEditForm.employeeNo || null,
                        hireDate: teacherEditForm.hireDate || null
                    })
                });
                const result = await response.json();
                if (result.code === 200) {
                    teacherSaveSuccess.value = true;
                    // 重新查询详情
                    if (selectedTeacher.value) {
                        await viewTeacherDetail(selectedTeacher.value);
                    } else {
                        await fetchMyTeacherInfo();
                    }
                    teacherEditing.value = false;
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '修改失败';
                }
            } catch (err) {
                console.error('修改教师信息异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                teacherSaveLoading.value = false;
            }
        }

        function backToTeacherList() {
            viewingTeacherDetail.value = false;
            teacherInfo.value = null;
            selectedTeacher.value = null;
            errorMsg.value = '';
            teacherEditing.value = false;
        }

        // ---------- 查询指定学生的详细信息（教师/管理员点击查看） ----------
        async function viewStudentDetail(student) {
            if (!student || !student.userId) return;

            loading.value = true;
            errorMsg.value = '';
            selectedStudent.value = student;
            viewingDetail.value = true;

            try {
                const response = await fetch(`/student/info?userId=${student.userId}`, {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    studentInfo.value = result.data;
                } else {
                    errorMsg.value = result.message || '查询学生详情失败';
                }
            } catch (err) {
                console.error('查询学生详情异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // 编辑状态
        const editing = ref(false);
        const editForm = reactive({
            id: null,
            name: '',
            gender: null,
            studentNo: '',
            idCard: '',
            collegeId: null,
            majorId: null,
            classId: null,
            enrollDate: '',
            graduateDate: ''
        });
        const saveLoading = ref(false);
        const saveSuccess = ref(false);

        // 模态框状态
        const showModal = ref(false);

        // 字典数据（学院、专业、班级下拉列表）
        const colleges = ref([]);
        const majors = ref([]);
        const classes = ref([]);

        // 根据选中的学院过滤专业
        const filteredMajors = computed(() => {
            const cId = editForm.collegeId;
            if (!cId) return majors.value;
            return majors.value.filter(m => m.collegeId === Number(cId));
        });

        // ---------- 获取字典数据 ----------
        async function fetchDictData() {
            try {
                const response = await fetch('/dict/all', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200 && result.data) {
                    colleges.value = result.data.colleges || [];
                    majors.value = result.data.majors || [];
                    classes.value = result.data.classes || [];
                }
            } catch (e) {
                console.error('获取字典数据失败', e);
            }
        }

        // ---------- 打开编辑模态框 ----------
        function openEditModal(student) {
            if (!student) return;
            // 填充表单数据
            editForm.id = student.studentId;
            editForm.name = student.name || '';
            editForm.gender = student.gender;
            editForm.studentNo = student.studentNo || '';
            editForm.idCard = student.idCard || '';
            editForm.collegeId = student.collegeId || null;
            editForm.majorId = student.majorId || null;
            editForm.classId = student.classId || null;
            editForm.enrollDate = student.enrollDate || '';
            editForm.graduateDate = student.graduateDate || '';
            saveSuccess.value = false;
            errorMsg.value = '';
            showModal.value = true;
        }

        // ---------- 关闭编辑模态框 ----------
        function closeModal() {
            showModal.value = false;
            saveSuccess.value = false;
            errorMsg.value = '';
        }

        // ---------- 进入编辑模式（教师/管理员） ----------
        function startEdit() {
            if (!studentInfo.value) return;
            editForm.id = studentInfo.value.studentId;
            editForm.name = studentInfo.value.name || '';
            editForm.gender = studentInfo.value.gender;
            editForm.studentNo = studentInfo.value.studentNo || '';
            editForm.idCard = studentInfo.value.idCard || '';
            editForm.collegeId = studentInfo.value.collegeId || null;
            editForm.majorId = studentInfo.value.majorId || null;
            editForm.classId = studentInfo.value.classId || null;
            editForm.enrollDate = studentInfo.value.enrollDate || '';
            editForm.graduateDate = studentInfo.value.graduateDate || '';
            editing.value = true;
            saveSuccess.value = false;
            errorMsg.value = '';
        }

        // ---------- 取消编辑 ----------
        function cancelEdit() {
            editing.value = false;
            saveSuccess.value = false;
            errorMsg.value = '';
        }

        // ---------- 快捷编辑（从列表弹出模态框） ----------
        function quickEditStudent(student) {
            if (!student) return;
            openEditModal(student);
        }

        // ---------- 保存修改（教师/管理员） ----------
        async function saveEdit() {
            errorMsg.value = '';
            saveSuccess.value = false;

            if (!editForm.id) {
                errorMsg.value = '学生记录ID缺失';
                return;
            }

            saveLoading.value = true;
            try {
                const response = await fetch('/student/update', {
                    method: 'POST',
                    headers: getHeaders(),
                    body: JSON.stringify({
                        id: editForm.id,
                        name: editForm.name || null,
                        gender: editForm.gender !== null && editForm.gender !== '' ? Number(editForm.gender) : null,
                        studentNo: editForm.studentNo || null,
                        idCard: editForm.idCard || null,
                        collegeId: editForm.collegeId !== null && editForm.collegeId !== '' ? Number(editForm.collegeId) : null,
                        majorId: editForm.majorId !== null && editForm.majorId !== '' ? Number(editForm.majorId) : null,
                        classId: editForm.classId !== null && editForm.classId !== '' ? Number(editForm.classId) : null,
                        enrollDate: editForm.enrollDate || null,
                        graduateDate: editForm.graduateDate || null
                    })
                });
                const result = await response.json();
                if (result.code === 200) {
                    saveSuccess.value = true;
                    // 重新查询详情
                    if (selectedStudent.value) {
                        await viewStudentDetail(selectedStudent.value);
                    }
                    editing.value = false;
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '修改失败';
                }
            } catch (err) {
                console.error('修改学生信息异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                saveLoading.value = false;
            }
        }

        // ---------- 返回列表（教师/管理员从详情返回） ----------
        function backToList() {
            viewingDetail.value = false;
            studentInfo.value = null;
            selectedStudent.value = null;
            errorMsg.value = '';
            editing.value = false;
        }

        // ---------- 查询毕业生信息 ----------
        async function fetchAllGraduates() {
            loading.value = true;
            errorMsg.value = '';

            try {
                const response = await fetch('/graduate/list', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    graduateList.value = result.data || [];
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                } else {
                    errorMsg.value = result.message || '查询毕业生信息失败';
                }
            } catch (err) {
                console.error('查询毕业生信息异常', err);
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        async function fetchMyGraduateInfo() {
            if (!userInfo.value || !userInfo.value.userId) return;

            try {
                const response = await fetch('/graduate/info', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    myGraduateInfo.value = result.data;
                }
            } catch (err) {
                console.error('查询我的毕业生信息异常', err);
            }
        }

        // ---------- 渲染图表 ----------
        function renderCharts() {
            const stats = destinationStats.value;
            if (!stats || !stats.byDestination) return;

            // 销毁旧图表
            if (pieChartInstance) {
                pieChartInstance.destroy();
                pieChartInstance = null;
            }
            if (barChartInstance) {
                barChartInstance.destroy();
                barChartInstance = null;
            }

            // ---- 饼状图：去向分布 ----
            const pieCtx = document.getElementById('pieChart');
            if (pieCtx) {
                const labels = [];
                const data = [];
                const colors = ['#27ae60', '#e67e22', '#8e44ad'];
                stats.byDestination.forEach(item => {
                    labels.push(destinationMap[item.destination] || '未知');
                    data.push(item.count);
                });
                try {
                    pieChartInstance = new Chart(pieCtx, {
                        type: 'pie',
                        data: {
                            labels: labels,
                            datasets: [{
                                data: data,
                                backgroundColor: colors.slice(0, data.length),
                                borderColor: '#fff',
                                borderWidth: 2
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: true,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: {
                                        font: { size: 13 },
                                        padding: 16
                                    }
                                }
                            }
                        }
                    });
                } catch (e) {
                    console.error('饼状图渲染失败', e);
                }
            }

            // ---- 柱状图：各年份各去向分布 ----
            const barCtx = document.getElementById('barChart');
            if (barCtx && stats.byYearAndDestination && stats.byYearAndDestination.length > 0) {
                const yearMap = {};
                stats.byYearAndDestination.forEach(item => {
                    const year = item.graduateYear;
                    if (!yearMap[year]) yearMap[year] = {};
                    yearMap[year][item.destination] = item.count;
                });
                const years = Object.keys(yearMap).sort();
                const destNames = { 1: '💼就业', 2: '📋考公', 3: '📚考研' };
                const barColors = ['#27ae60', '#e67e22', '#8e44ad'];
                const datasets = [];
                [1, 2, 3].forEach(dest => {
                    const d = years.map(y => yearMap[y][dest] || 0);
                    if (d.some(v => v > 0)) {
                        datasets.push({
                            label: destNames[dest],
                            data: d,
                            backgroundColor: barColors[dest - 1],
                            borderRadius: 4
                        });
                    }
                });
                try {
                    barChartInstance = new Chart(barCtx, {
                        type: 'bar',
                        data: {
                            labels: years.map(y => y + '年'),
                            datasets: datasets
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: true,
                            scales: {
                                x: { stacked: true, grid: { display: false } },
                                y: { stacked: true, beginAtZero: true, ticks: { precision: 0 } }
                            },
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: { font: { size: 12 }, padding: 12 }
                                }
                            }
                        }
                    });
                } catch (e) {
                    console.error('柱状图渲染失败', e);
                }
            }
        }

        // ---------- 查询毕业生去向统计（教师/管理员） ----------
        async function fetchDestinationStatistics() {
            statsLoading.value = true;
            try {
                const response = await fetch('/graduate/statistics', {
                    method: 'GET',
                    headers: getHeaders()
                });
                const result = await response.json();
                if (result.code === 200) {
                    destinationStats.value = result.data;
                    // 等待 Vue 更新 DOM 后渲染图表
                    Vue.nextTick(() => {
                        renderCharts();
                    });
                } else if (result.code === 401) {
                    localStorage.removeItem('userInfo');
                    window.location.href = '/index.html';
                }
            } catch (err) {
                console.error('查询毕业生去向统计异常', err);
            } finally {
                statsLoading.value = false;
            }
        }

        // ---------- 退出登录 ----------
        async function doLogout() {
            loading.value = true;
            try {
                const token = userInfo.value?.token;
                if (token) {
                    await fetch('/user/logout', {
                        method: 'POST',
                        headers: { 'Authorization': 'Bearer ' + token }
                    });
                }
            } catch (e) {
                console.error('注销请求失败', e);
            } finally {
                localStorage.removeItem('userInfo');
                window.location.href = '/index.html';
            }
        }

        // ---------- 页面跳转 ----------
        function goToChangePassword() {
            window.location.href = '/change-password.html';
        }

        // ---------- 生命周期 ----------
        onMounted(() => {
            loadUserInfo();
            // 等待 DOM 更新后查询
            setTimeout(() => {
                fetchDictData();
                const role = userInfo.value?.role;
                if (role >= 2) {
                    // 教师/管理员：加载学生列表和教师列表
                    fetchAllStudents();
                    fetchAllTeachers();
                    // 加载毕业生列表（教师/管理员查看所有毕业生）
                    fetchAllGraduates();
                    // 加载毕业生去向统计
                    fetchDestinationStatistics();
                    if (role === 2) {
                        fetchMyTeacherInfo();
                    }
                } else {
                    // 学生：加载自己的学生信息
                    fetchMyStudentInfo();
                    // 学生查看自己的毕业生记录
                    fetchMyGraduateInfo();
                }
            }, 0);
        });

        return {
            loading, errorMsg,
            userInfo, studentInfo,
            studentList, selectedStudent,
            viewingDetail, searchKeyword,
            filteredStudentList,
            genderMap, roleMap,
            isTeacherOrAdmin, isAdmin,
            editing, editForm, saveLoading, saveSuccess,
            showModal, colleges, majors, classes, filteredMajors,
            doLogout, goToChangePassword,
            viewStudentDetail, backToList,
            startEdit, cancelEdit, saveEdit,
            quickEditStudent, openEditModal, closeModal,
            fetchDictData,
            // 教师管理
            teacherInfo, teacherList, selectedTeacher,
            viewingTeacherDetail, searchTeacherKeyword,
            filteredTeacherList,
            teacherEditing, teacherEditForm, teacherSaveLoading, teacherSaveSuccess,
            showTeacherModal,
            fetchMyTeacherInfo, fetchAllTeachers,
            viewTeacherDetail, backToTeacherList,
            startTeacherEdit, cancelTeacherEdit, saveTeacherEdit,
            openTeacherEditModal, closeTeacherModal,
            // 毕业生管理
            graduateList, myGraduateInfo, searchGraduateKeyword, filteredGraduateList,
            destinationMap,
            destinationStats, statsLoading,
            fetchAllGraduates, fetchMyGraduateInfo, fetchDestinationStatistics,
            // 侧边栏 Tab
            activeTab, tabList, switchTab
        };
    }
}).mount('#app');
