const { createApp, ref, reactive, computed, onMounted } = Vue;

createApp({
    setup() {
        const loading = ref(false);
        const errorMsg = ref('');

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

        // 搜索关键词（教师/管理员使用）
        const searchKeyword = ref('');

        // 性别映射
        const genderMap = { 0: '未知', 1: '男', 2: '女' };

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
            enrollDate: '',
            graduateDate: ''
        });
        const saveLoading = ref(false);
        const saveSuccess = ref(false);

        // ---------- 进入编辑模式（教师/管理员） ----------
        function startEdit() {
            if (!studentInfo.value) return;
            editForm.id = studentInfo.value.studentId;
            editForm.name = studentInfo.value.name || '';
            editForm.gender = studentInfo.value.gender;
            editForm.studentNo = studentInfo.value.studentNo || '';
            editForm.idCard = studentInfo.value.idCard || '';
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
                if (isTeacherOrAdmin.value) {
                    fetchAllStudents();
                } else {
                    fetchMyStudentInfo();
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
            doLogout, goToChangePassword,
            viewStudentDetail, backToList,
            startEdit, cancelEdit, saveEdit
        };
    }
}).mount('#app');
