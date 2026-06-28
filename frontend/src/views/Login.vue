<script setup>
import { useLogin } from '@/composables/useLogin'
import '@/assets/login.css'

const { loading, errorMsg, form, doLogin } = useLogin()
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <h2>校友管理系统登录</h2>

      <div class="form-group">
        <label>账号 / 手机号</label>
        <input
          v-model="form.account"
          type="text"
          placeholder="请输入用户名或手机号"
          @keyup.enter="doLogin"
        />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          @keyup.enter="doLogin"
        />
      </div>
      <button class="login-btn" :disabled="loading" @click="doLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <div class="switch-link">
        <router-link to="/register">注册账号</router-link>
      </div>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    </div>
  </div>
</template>
