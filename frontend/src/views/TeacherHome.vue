<script setup>
import { useTeacherHome } from './js/teacher-home.js'
const {
  userInfo, activeTab, teacherInfo, students, graduates,
  editMode, editForm, loading, roleMap, destinationMap, genderMap,
  notSupervisor, switchTab, saveProfile, cancelEdit, logout
} = useTeacherHome()
</script>

<template>
  <div class="home-page">
    <header class="top-nav">
      <span class="nav-title">校友管理系统 - 教师端</span>
      <div class="nav-right">
        <span class="user-greeting" v-if="userInfo">
          {{ userInfo.username }}（{{ roleMap[userInfo.role] || '未知' }}）
        </span>
        <el-button type="danger" plain size="small" @click="logout">退出登录</el-button>
      </div>
    </header>

    <div class="home-layout">
      <!-- 左侧边栏 -->
      <el-menu
        :default-active="activeTab"
        class="sidebar-menu"
        @select="switchTab"
      >
        <div class="sidebar-header">功能导航</div>
        <el-menu-item index="students">
          <el-icon><Document /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="graduates">
          <el-icon><Reading /></el-icon>
          <span>毕业生管理</span>
        </el-menu-item>
        <el-menu-item index="profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>

      <!-- 主内容区 -->
      <main class="home-main-content">
        <!-- 学生管理 -->
        <div v-if="activeTab === 'students'" class="tab-content">
          <div class="tab-header">
            <h2>学生管理</h2>
            <span class="tab-desc">我指导的学生列表</span>
          </div>
          <el-card shadow="never">
            <el-table
              :data="students"
              v-loading="loading.students"
              border
              stripe
              style="width: 100%"
              empty-text="暂无指导的学生记录"
            >
              <el-table-column prop="studentNo" label="学号" width="120" />
              <el-table-column label="姓名" width="100">
                <template #default="{ row }">
                  {{ row.name || row.studentName || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="genderName" label="性别" width="60" />
              <el-table-column prop="collegeName" label="学院" min-width="120" />
              <el-table-column prop="majorName" label="专业" min-width="120" />
              <el-table-column prop="className" label="班级" min-width="100" />
              <el-table-column label="年级" width="80">
                <template #default="{ row }">
                  {{ row.grade ? row.grade + '年级' : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.userStatus === 1 ? 'success' : 'info'" size="small">
                    {{ row.statusName || (row.userStatus === 1 ? '在校' : '离校') }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 毕业生管理 -->
        <div v-if="activeTab === 'graduates'" class="tab-content">
          <div class="tab-header">
            <h2>毕业生管理</h2>
            <span class="tab-desc">查看毕业生信息</span>
          </div>
          <el-card shadow="never">
            <el-table
              :data="graduates"
              v-loading="loading.graduates"
              border
              stripe
              style="width: 100%"
              :empty-text="'暂无毕业生记录'"
            >
              <el-table-column prop="studentNo" label="学号" width="120" />
              <el-table-column prop="realName" label="姓名" width="100" />
              <el-table-column prop="collegeName" label="学院" min-width="120" />
              <el-table-column prop="majorName" label="专业" min-width="120" />
              <el-table-column prop="graduateYear" label="毕业年份" width="100" />
              <el-table-column label="去向" width="80">
                <template #default="{ row }">
                  <el-tag type="primary" size="small">
                    {{ destinationMap[row.destination] || '未知' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="destinationDetail" label="去向详情" min-width="150" />
              <el-table-column prop="certificateNo" label="毕业证编号" width="160" />
            </el-table>
          </el-card>
        </div>

        <!-- 个人信息 -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <div class="tab-header">
            <h2>个人信息</h2>
            <span class="tab-desc">{{ editMode ? '编辑个人信息' : '查看个人信息' }}</span>
          </div>
          <el-card shadow="never" v-loading="!teacherInfo">
            <div v-if="teacherInfo">
              <el-form :model="editForm" label-width="100px" :disabled="!editMode">
                <el-form-item label="用户名">
                  <span>{{ teacherInfo.username || '-' }}</span>
                </el-form-item>
                <el-form-item label="真实姓名">
                  <el-input v-if="editMode" v-model="editForm.name" placeholder="请输入姓名" />
                  <span v-else>{{ teacherInfo.realName || '-' }}</span>
                </el-form-item>
                <el-form-item label="性别">
                  <el-select v-if="editMode" v-model="editForm.gender" placeholder="请选择性别">
                    <el-option :value="0" label="未知" />
                    <el-option :value="1" label="男" />
                    <el-option :value="2" label="女" />
                  </el-select>
                  <span v-else>{{ teacherInfo.genderName || '未知' }}</span>
                </el-form-item>
                <el-form-item label="手机号">
                  <span>{{ teacherInfo.phone || '-' }}</span>
                </el-form-item>
                <el-form-item label="工号">
                  <el-input v-if="editMode" v-model="editForm.employeeNo" placeholder="请输入工号" />
                  <span v-else>{{ teacherInfo.employeeNo || '-' }}</span>
                </el-form-item>
                <el-form-item label="职称">
                  <el-input v-if="editMode" v-model="editForm.title" placeholder="请输入职称" />
                  <span v-else>{{ teacherInfo.title || '-' }}</span>
                </el-form-item>
                <el-form-item label="学院">
                  <span>{{ teacherInfo.collegeName || '-' }}</span>
                </el-form-item>
                <el-form-item label="入职时间">
                  <span>{{ teacherInfo.hireDate || '-' }}</span>
                </el-form-item>
                <el-form-item label="用户状态">
                  <el-tag :type="teacherInfo.userStatus === 1 ? 'success' : 'info'" size="small">
                    {{ teacherInfo.statusName || (teacherInfo.userStatus === 1 ? '在职' : '离职') }}
                  </el-tag>
                </el-form-item>
              </el-form>
              <div style="margin-top: 20px; text-align: center;">
                <el-button v-if="!editMode" type="primary" @click="editMode = true">修改信息</el-button>
                <template v-else>
                  <el-button type="primary" @click="saveProfile">保存</el-button>
                  <el-button @click="cancelEdit">取消</el-button>
                </template>
              </div>
            </div>
          </el-card>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  flex-shrink: 0;
}
.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-greeting {
  font-size: 14px;
  color: #606266;
}
.home-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.sidebar-menu {
  width: 200px;
  height: 100%;
  border-right: 1px solid #e4e7ed;
  flex-shrink: 0;
}
.sidebar-header {
  padding: 16px 20px 8px;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}
.home-main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.tab-content {
  max-width: 1200px;
}
.tab-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}
.tab-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}
.tab-desc {
  font-size: 14px;
  color: #909399;
}
</style>
