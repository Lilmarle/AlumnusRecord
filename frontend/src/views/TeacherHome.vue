<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/api/index.js'
import '@/assets/home.css'

const router = useRouter()
const userInfo = ref(null)
const activeTab = ref('students') // students, graduates, profile

// 教师信息
const teacherInfo = ref(null)
const supervisorInfo = ref(null)
const students = ref([])
const graduates = ref([])

// 个人信息编辑
const editMode = ref(false)
const editForm = ref({
  name: '',
  gender: null,
  phone: '',
  title: '',
  employeeNo: ''
})

// 加载状态
const loading = ref({
  students: false,
  graduates: false,
  profile: false
})

const roleMap = { 1: '学生', 2: '老师', 3: '管理员' }

onMounted(async () => {
  const raw = localStorage.getItem('userInfo')
  if (raw) {
    userInfo.value = JSON.parse(raw)
  }
  // 加载教师信息
  await loadTeacherInfo()
  // 默认加载学生列表
  if (activeTab.value === 'students') {
    await loadStudents()
  }
})

// 加载教师信息
async function loadTeacherInfo() {
  try {
    const res = await get('/teacher/info')
    if (res.code === 1) {
      teacherInfo.value = res.data
      editForm.value = {
        name: res.data.realName || '',
        gender: res.data.gender,
        phone: res.data.phone || '',
        title: res.data.title || '',
        employeeNo: res.data.employeeNo || ''
      }
    }
  } catch (e) {
    console.error('获取教师信息失败:', e)
  }
}

// 获取导师记录ID
async function getSupervisorId() {
  if (supervisorInfo.value) return supervisorInfo.value.supervisorId
  try {
    const res = await get('/supervisor/info')
    if (res.code === 1) {
      supervisorInfo.value = res.data
      return res.data.supervisorId
    }
  } catch (e) {
    console.error('获取导师信息失败:', e)
  }
  return null
}

// 加载学生列表
async function loadStudents() {
  loading.value.students = true
  students.value = []
  try {
    const supId = await getSupervisorId()
    if (!supId) {
      loading.value.students = false
      return
    }
    const res = await get('/student-supervisor/list-by-supervisor', { supId })
    if (res.code === 1) {
      students.value = res.data || []
    }
  } catch (e) {
    console.error('获取学生列表失败:', e)
  } finally {
    loading.value.students = false
  }
}

// 加载毕业生列表
async function loadGraduates() {
  loading.value.graduates = true
  graduates.value = []
  try {
    const res = await get('/graduate/list')
    if (res.code === 1) {
      graduates.value = res.data || []
    }
  } catch (e) {
    console.error('获取毕业生列表失败:', e)
  } finally {
    loading.value.graduates = false
  }
}

// 切换标签
async function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'students' && students.value.length === 0) {
    await loadStudents()
  } else if (tab === 'graduates' && graduates.value.length === 0) {
    await loadGraduates()
  } else if (tab === 'profile' && !teacherInfo.value) {
    await loadTeacherInfo()
  }
}

// 保存个人信息
async function saveProfile() {
  try {
    const payload = {
      id: teacherInfo.value.teacherId,
      name: editForm.value.name,
      gender: editForm.value.gender,
      title: editForm.value.title,
      employeeNo: editForm.value.employeeNo
    }
    const res = await post('/teacher/update', payload)
    if (res.code === 1) {
      editMode.value = false
      await loadTeacherInfo()
      alert('个人信息修改成功')
    } else {
      alert(res.message || '修改失败')
    }
  } catch (e) {
    alert('保存失败: ' + e.message)
  }
}

// 取消编辑
function cancelEdit() {
  editMode.value = false
  if (teacherInfo.value) {
    editForm.value = {
      name: teacherInfo.value.realName || '',
      gender: teacherInfo.value.gender,
      phone: teacherInfo.value.phone || '',
      title: teacherInfo.value.title || '',
      employeeNo: teacherInfo.value.employeeNo || ''
    }
  }
}

// 去向文本映射
const destinationMap = { 1: '就业', 2: '考公', 3: '考研' }

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/')
}
</script>

<template>
  <div class="home-page">
    <header class="top-nav">
      <span class="nav-title">校友管理系统 - 教师端</span>
      <div class="nav-right">
        <span class="user-greeting" v-if="userInfo">
          {{ userInfo.username }}（{{ roleMap[userInfo.role] || '未知' }}）
        </span>
        <button class="nav-btn nav-btn-logout" @click="logout">退出登录</button>
      </div>
    </header>

    <div class="home-layout">
      <!-- 左侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-header">功能导航</div>
        <nav class="sidebar-nav">
          <button
            class="sidebar-btn"
            :class="{ active: activeTab === 'students' }"
            @click="switchTab('students')"
          >
            <span class="sidebar-icon">📋</span>
            学生管理
          </button>
          <button
            class="sidebar-btn"
            :class="{ active: activeTab === 'graduates' }"
            @click="switchTab('graduates')"
          >
            <span class="sidebar-icon">🎓</span>
            毕业生管理
          </button>
          <button
            class="sidebar-btn"
            :class="{ active: activeTab === 'profile' }"
            @click="switchTab('profile')"
          >
            <span class="sidebar-icon">👤</span>
            个人信息
          </button>
        </nav>
      </aside>

      <!-- 主内容区 -->
      <main class="home-main-content">
        <!-- 学生管理 -->
        <div v-if="activeTab === 'students'" class="tab-content">
          <div class="tab-header">
            <h2>学生管理</h2>
            <span class="tab-desc">我指导的学生列表</span>
          </div>
          <div class="content-card">
            <div v-if="loading.students" class="loading-tip">加载中...</div>
            <div v-else-if="students.length === 0" class="empty-tip">
              暂无指导的学生记录
            </div>
            <table v-else class="data-table">
              <thead>
                <tr>
                  <th>学号</th>
                  <th>姓名</th>
                  <th>性别</th>
                  <th>学院</th>
                  <th>专业</th>
                  <th>班级</th>
                  <th>年级</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(stu, idx) in students" :key="idx">
                  <td>{{ stu.studentNo || '-' }}</td>
                  <td>{{ stu.name || stu.studentName || '-' }}</td>
                  <td>{{ stu.genderName || '-' }}</td>
                  <td>{{ stu.collegeName || '-' }}</td>
                  <td>{{ stu.majorName || '-' }}</td>
                  <td>{{ stu.className || '-' }}</td>
                  <td>{{ stu.grade ? stu.grade + '年级' : '-' }}</td>
                  <td>
                    <span class="status-badge" :class="stu.userStatus === 1 ? 'status-active' : 'status-inactive'">
                      {{ stu.statusName || (stu.userStatus === 1 ? '在校' : '离校') }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 毕业生管理 -->
        <div v-if="activeTab === 'graduates'" class="tab-content">
          <div class="tab-header">
            <h2>毕业生管理</h2>
            <span class="tab-desc">查看毕业生信息</span>
          </div>
          <div class="content-card">
            <div v-if="loading.graduates" class="loading-tip">加载中...</div>
            <div v-else-if="graduates.length === 0" class="empty-tip">
              暂无毕业生记录
            </div>
            <table v-else class="data-table">
              <thead>
                <tr>
                  <th>学号</th>
                  <th>姓名</th>
                  <th>学院</th>
                  <th>专业</th>
                  <th>毕业年份</th>
                  <th>去向</th>
                  <th>去向详情</th>
                  <th>毕业证编号</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(grad, idx) in graduates" :key="idx">
                  <td>{{ grad.studentNo || '-' }}</td>
                  <td>{{ grad.name || '-' }}</td>
                  <td>{{ grad.collegeName || '-' }}</td>
                  <td>{{ grad.majorName || '-' }}</td>
                  <td>{{ grad.graduateYear || '-' }}</td>
                  <td>
                    <span class="destination-tag">
                      {{ destinationMap[grad.destination] || '未知' }}
                    </span>
                  </td>
                  <td>{{ grad.destinationDetail || '-' }}</td>
                  <td>{{ grad.certificateNo || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 个人信息 -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <div class="tab-header">
            <h2>个人信息</h2>
            <span class="tab-desc">{{ editMode ? '编辑个人信息' : '查看个人信息' }}</span>
          </div>
          <div class="content-card profile-card">
            <div v-if="!teacherInfo" class="loading-tip">加载中...</div>
            <div v-else>
              <div class="profile-info">
                <div class="info-row">
                  <label>用户名</label>
                  <span>{{ teacherInfo.username || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>真实姓名</label>
                  <input v-if="editMode" v-model="editForm.name" placeholder="请输入姓名" />
                  <span v-else>{{ teacherInfo.realName || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>性别</label>
                  <select v-if="editMode" v-model.number="editForm.gender">
                    <option :value="0">未知</option>
                    <option :value="1">男</option>
                    <option :value="2">女</option>
                  </select>
                  <span v-else>{{ teacherInfo.genderName || '未知' }}</span>
                </div>
                <div class="info-row">
                  <label>手机号</label>
                  <span>{{ teacherInfo.phone || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>工号</label>
                  <input v-if="editMode" v-model="editForm.employeeNo" placeholder="请输入工号" />
                  <span v-else>{{ teacherInfo.employeeNo || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>职称</label>
                  <input v-if="editMode" v-model="editForm.title" placeholder="请输入职称" />
                  <span v-else>{{ teacherInfo.title || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>学院</label>
                  <span>{{ teacherInfo.collegeName || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>入职时间</label>
                  <span>{{ teacherInfo.hireDate || '-' }}</span>
                </div>
                <div class="info-row">
                  <label>用户状态</label>
                  <span>{{ teacherInfo.statusName || (teacherInfo.userStatus === 1 ? '在职' : '离职') }}</span>
                </div>
              </div>
              <div class="profile-actions" v-if="!editMode">
                <button class="nav-btn edit-btn" @click="editMode = true">修改信息</button>
              </div>
              <div class="profile-actions" v-else>
                <button class="nav-btn save-btn" @click="saveProfile">保存</button>
                <button class="nav-btn cancel-btn" @click="cancelEdit">取消</button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.home-layout {
  display: flex;
  padding-top: 60px;
  min-height: 100vh;
}

/* ===== 侧边栏 ===== */
.sidebar {
  width: 200px;
  min-width: 200px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
}

.sidebar-header {
  padding: 20px 20px 12px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding: 0 12px;
  gap: 4px;
}

.sidebar-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 15px;
  color: #444;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  width: 100%;
}

.sidebar-btn:hover {
  background: #f0f5ff;
  color: #1a3a5c;
}

.sidebar-btn.active {
  background: #e6f0ff;
  color: #1a3a5c;
  font-weight: 600;
}

.sidebar-icon {
  font-size: 18px;
}

/* ===== 主内容区 ===== */
.home-main-content {
  flex: 1;
  padding: 24px 32px;
  background: #f5f7fa;
  overflow-y: auto;
}

.tab-header {
  margin-bottom: 20px;
}

.tab-header h2 {
  font-size: 22px;
  color: #1a3a5c;
  margin: 0 0 6px;
}

.tab-desc {
  font-size: 13px;
  color: #999;
}

/* ===== 内容卡片 ===== */
.content-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.loading-tip,
.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 14px;
}

/* ===== 表格 ===== */
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th {
  background: #fafafa;
  padding: 12px 10px;
  text-align: left;
  font-weight: 600;
  color: #555;
  border-bottom: 2px solid #e8e8e8;
  white-space: nowrap;
}

.data-table td {
  padding: 12px 10px;
  border-bottom: 1px solid #f0f0f0;
  color: #333;
}

.data-table tbody tr:hover {
  background: #fafbff;
}

/* ===== 状态徽章 ===== */
.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-inactive {
  background: #fbe9e7;
  color: #c62828;
}

.destination-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  background: #e3f2fd;
  color: #1565c0;
}

/* ===== 个人信息 ===== */
.profile-card {
  max-width: 600px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row label {
  width: 100px;
  font-size: 14px;
  color: #888;
  flex-shrink: 0;
}

.info-row span {
  font-size: 14px;
  color: #333;
}

.info-row input,
.info-row select {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
  max-width: 300px;
}

.info-row input:focus,
.info-row select:focus {
  border-color: #1a3a5c;
  box-shadow: 0 0 0 2px rgba(26, 58, 92, 0.1);
}

/* ===== 操作按钮 ===== */
.profile-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.edit-btn {
  background: #1a3a5c;
  color: #fff;
  border: none;
  padding: 9px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.edit-btn:hover {
  background: #15314d;
}

.save-btn {
  background: #1a3a5c;
  color: #fff;
  border: none;
  padding: 9px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.save-btn:hover {
  background: #15314d;
}

.cancel-btn {
  background: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
  padding: 9px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  color: #333;
  border-color: #bbb;
}
</style>
