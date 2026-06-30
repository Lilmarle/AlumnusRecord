<script setup>
import { useAdminHome } from './js/admin-home.js'
const {
  userInfo, activeTab, students, graduates, editMode, editForm,
  showEditModal, editingStudent, editStudentForm, dictData,
  filteredMajors, filteredClasses, selectedGrade,
  loading, roleMap, genderMap, destinationMap,
  showGraduateDetail, graduateDetail,
  switchTab, openEditStudent, saveStudent, saveProfile, cancelEdit, logout,
  openGraduateDetail, closeGraduateDetail
} = useAdminHome()
</script>

<template>
  <div class="home-page">
    <header class="top-nav">
      <span class="nav-title">校友管理系统 - 管理端</span>
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
            <span class="tab-desc">查看所有学生信息（共 {{ students.length }} 人）</span>
          </div>
          <el-card shadow="never">
            <el-table
              :data="students"
              v-loading="loading.students"
              border
              stripe
              style="width: 100%"
              :empty-text="'暂无学生记录'"
            >
              <el-table-column prop="studentNo" label="学号" width="120" />
              <el-table-column prop="name" label="姓名" width="100" />
              <el-table-column prop="genderName" label="性别" width="60" />
              <el-table-column prop="collegeName" label="学院" min-width="120" />
              <el-table-column prop="majorName" label="专业" min-width="120" />
              <el-table-column prop="className" label="班级" min-width="100" />
              <el-table-column label="年级" width="80">
                <template #default="{ row }">
                  {{ row.grade ? row.grade + '年级' : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="counselorName" label="辅导员" width="90" />
              <el-table-column prop="supervisorName" label="导师" width="90" />
              <el-table-column label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.userStatus === 1 ? 'success' : 'info'" size="small">
                    {{ row.statusName || (row.userStatus === 1 ? '在校' : '离校') }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="90" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="openEditStudent(row)">修改</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 学生编辑弹窗 -->
        <el-dialog
          v-model="showEditModal"
          title="修改学生信息"
          width="600px"
          :close-on-click-modal="false"
        >
          <el-form :model="editStudentForm" label-width="100px">
            <el-form-item label="学号">
              <el-input v-model="editStudentForm.studentNo" placeholder="学号" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="editStudentForm.name" placeholder="姓名" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="editStudentForm.gender" placeholder="请选择性别">
                <el-option :value="0" label="未知" />
                <el-option :value="1" label="男" />
                <el-option :value="2" label="女" />
              </el-select>
            </el-form-item>
            <el-form-item label="学院">
              <el-select
                v-model="editStudentForm.collegeId"
                placeholder="请选择学院"
                @change="editStudentForm.majorId = null; editStudentForm.classId = null"
              >
                <el-option
                  v-for="col in dictData.colleges"
                  :key="col.id"
                  :value="col.id"
                  :label="col.collegeName"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="专业">
              <el-select
                v-model="editStudentForm.majorId"
                placeholder="请选择专业"
                :disabled="!editStudentForm.collegeId"
                @change="editStudentForm.classId = null"
              >
                <el-option
                  v-for="maj in filteredMajors"
                  :key="maj.id"
                  :value="maj.id"
                  :label="maj.majorName"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="班级">
              <el-select
                v-model="editStudentForm.classId"
                placeholder="请选择班级"
                :disabled="!editStudentForm.majorId"
              >
                <el-option
                  v-for="cls in filteredClasses"
                  :key="cls.id"
                  :value="cls.id"
                  :label="cls.className"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="年级">
              <el-input :model-value="selectedGrade ? selectedGrade + '年级' : ''" placeholder="请先选择班级" disabled />
            </el-form-item>
            <el-form-item label="入校时间">
              <el-input v-model="editStudentForm.enrollDate" placeholder="入校时间" />
            </el-form-item>
            <el-form-item label="毕业时间">
              <el-input v-model="editStudentForm.graduateDate" placeholder="毕业时间" />
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="editStudentForm.idCard" placeholder="身份证号" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button type="primary" @click="saveStudent">保存修改</el-button>
          </template>
        </el-dialog>

        <!-- 毕业生管理 -->
        <div v-if="activeTab === 'graduates'" class="tab-content">
          <div class="tab-header">
            <h2>毕业生管理</h2>
            <span class="tab-desc">查看所有毕业生信息（共 {{ graduates.length }} 人）</span>
          </div>
          <el-card shadow="never" style="overflow-x: auto;">
            <el-table
              :data="graduates"
              v-loading="loading.graduates"
              border
              stripe
              style="width: 100%"
              :empty-text="'暂无毕业生记录'"
            >
              <el-table-column prop="studentNo" label="学号" width="110" />
              <el-table-column prop="realName" label="姓名" width="90" />
              <el-table-column label="性别" width="60">
                <template #default="{ row }">
                  {{ genderMap[row.gender] || row.genderName || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="phone" label="手机号" width="120" />
              <el-table-column prop="collegeName" label="学院" width="130" />
              <el-table-column prop="majorName" label="专业" width="120" />
              <el-table-column prop="graduateYear" label="毕业年份" width="90" />
              <el-table-column label="去向" width="70">
                <template #default="{ row }">
                  <el-tag type="primary" size="small">
                    {{ destinationMap[row.destination] || '未知' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="destinationDetail" label="去向详情" width="130" />
              <el-table-column prop="counselorName" label="辅导员" width="80" />
              <el-table-column prop="supervisorName" label="导师" width="80" />
              <el-table-column label="操作" width="70" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="openGraduateDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 毕业生详情弹窗 -->
        <el-dialog
          v-model="showGraduateDetail"
          title="毕业生详细信息"
          width="800px"
          :close-on-click-modal="false"
          @close="closeGraduateDetail"
        >
          <template v-if="graduateDetail">
            <el-descriptions :column="2" border size="small">
              <template #title>
                <span style="font-weight: 600; font-size: 16px;">基本信息</span>
              </template>
              <el-descriptions-item label="学号" :span="1">{{ graduateDetail.studentNo || '-' }}</el-descriptions-item>
              <el-descriptions-item label="姓名" :span="1">{{ graduateDetail.realName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="性别" :span="1">{{ genderMap[graduateDetail.gender] || graduateDetail.genderName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="手机号" :span="1">{{ graduateDetail.phone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="学院" :span="1">{{ graduateDetail.collegeName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="专业" :span="1">{{ graduateDetail.majorName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="班级" :span="1">{{ graduateDetail.className || '-' }}</el-descriptions-item>
              <el-descriptions-item label="年级" :span="1">{{ graduateDetail.grade ? graduateDetail.grade + '年级' : '-' }}</el-descriptions-item>
              <el-descriptions-item label="身份证号" :span="1">{{ graduateDetail.idCard || '-' }}</el-descriptions-item>
              <el-descriptions-item label="用户状态" :span="1">
                <el-tag :type="graduateDetail.userStatus === 1 ? 'success' : 'info'" size="small">
                  {{ graduateDetail.userStatus === 1 ? '在校' : '离校' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <el-descriptions :column="2" border size="small">
              <template #title>
                <span style="font-weight: 600; font-size: 16px;">毕业信息</span>
              </template>
              <el-descriptions-item label="毕业年份" :span="1">{{ graduateDetail.graduateYear ? graduateDetail.graduateYear + '年' : '-' }}</el-descriptions-item>
              <el-descriptions-item label="去向" :span="1">
                <el-tag type="primary" size="small">
                  {{ destinationMap[graduateDetail.destination] || '未知' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="去向详情" :span="2">{{ graduateDetail.destinationDetail || '-' }}</el-descriptions-item>
              <el-descriptions-item label="毕业证编号" :span="2">{{ graduateDetail.certificateNo || '-' }}</el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <el-descriptions :column="2" border size="small">
              <template #title>
                <span style="font-weight: 600; font-size: 16px;">辅导员信息</span>
              </template>
              <el-descriptions-item label="辅导员姓名" :span="1">
                <span v-if="graduateDetail.counselorName">
                  <el-avatar
                    v-if="graduateDetail.counselorAvatar"
                    :src="graduateDetail.counselorAvatar"
                    size="small"
                    style="vertical-align: middle; margin-right: 6px;"
                  />
                  {{ graduateDetail.counselorName }}
                </span>
                <span v-else class="no-data">未分配</span>
              </el-descriptions-item>
              <el-descriptions-item label="辅导员职称" :span="1">
                <span v-if="graduateDetail.counselorTitle">{{ graduateDetail.counselorTitle }}</span>
                <span v-else class="no-data">-</span>
              </el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <el-descriptions :column="2" border size="small">
              <template #title>
                <span style="font-weight: 600; font-size: 16px;">导师信息</span>
              </template>
              <el-descriptions-item label="导师姓名" :span="1">
                <span v-if="graduateDetail.supervisorName">
                  <el-avatar
                    v-if="graduateDetail.supervisorAvatar"
                    :src="graduateDetail.supervisorAvatar"
                    size="small"
                    style="vertical-align: middle; margin-right: 6px;"
                  />
                  {{ graduateDetail.supervisorName }}
                </span>
                <span v-else class="no-data">未分配</span>
              </el-descriptions-item>
              <el-descriptions-item label="导师职称" :span="1">
                <span v-if="graduateDetail.supervisorTitle">{{ graduateDetail.supervisorTitle }}</span>
                <span v-else class="no-data">-</span>
              </el-descriptions-item>
            </el-descriptions>
          </template>
          <template #footer>
            <el-button type="primary" @click="closeGraduateDetail">关闭</el-button>
          </template>
        </el-dialog>

        <!-- 个人信息 -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <div class="tab-header">
            <h2>个人信息</h2>
            <span class="tab-desc">{{ editMode ? '编辑个人信息' : '查看个人信息' }}</span>
          </div>
          <el-card shadow="never" v-loading="!userInfo">
            <div v-if="userInfo">
              <el-form :model="editForm" label-width="100px" :disabled="!editMode">
                <el-form-item label="用户名">
                  <span>{{ userInfo.username || '-' }}</span>
                </el-form-item>
                <el-form-item label="姓名">
                  <el-input v-if="editMode" v-model="editForm.name" placeholder="请输入姓名" />
                  <span v-else>{{ userInfo.username || '-' }}</span>
                </el-form-item>
                <el-form-item label="角色">
                  <el-tag type="warning" size="small">{{ roleMap[userInfo.role] || '未知' }}</el-tag>
                </el-form-item>
                <el-form-item label="性别">
                  <el-select v-if="editMode" v-model="editForm.gender" placeholder="请选择性别">
                    <el-option :value="0" label="未知" />
                    <el-option :value="1" label="男" />
                    <el-option :value="2" label="女" />
                  </el-select>
                  <span v-else>{{ genderMap[editForm.gender] || '未知' }}</span>
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
.no-data {
  color: #c0c4cc;
  font-style: italic;
}
</style>
