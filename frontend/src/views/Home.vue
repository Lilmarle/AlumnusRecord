<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import '@/assets/home.css'

const router = useRouter()
const userInfo = ref(null)

onMounted(() => {
  const raw = localStorage.getItem('userInfo')
  if (raw) {
    userInfo.value = JSON.parse(raw)
  }
})

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/')
}

const roleMap = { 1: '学生', 2: '老师', 3: '管理员' }
</script>

<template>
  <div class="home-page">
    <header class="top-nav">
      <span class="nav-title">校友管理系统</span>
      <div class="nav-right">
        <span class="user-greeting" v-if="userInfo">
          {{ userInfo.username }}（{{ roleMap[userInfo.role] || '未知' }}）
        </span>
        <button class="nav-btn nav-btn-logout" @click="logout">退出登录</button>
      </div>
    </header>

    <main class="home-main">
      <div class="welcome-card">
        <h2>欢迎使用校友管理系统</h2>
        <p v-if="userInfo">
          当前用户：{{ userInfo.username }} | 角色：{{ roleMap[userInfo.role] || '未知' }}
        </p>
      </div>
    </main>
  </div>
</template>

