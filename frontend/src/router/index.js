import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true, roles: [1] }
  },
  {
    path: '/teacher',
    name: 'TeacherHome',
    component: () => import('@/views/TeacherHome.vue'),
    meta: { requiresAuth: true, roles: [2] }
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('@/views/AdminHome.vue'),
    meta: { requiresAuth: true, roles: [3] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 根据角色获取对应的首页路由名称
 */
function getHomeRouteName(role) {
  const map = { 1: 'Home', 2: 'TeacherHome', 3: 'AdminHome' }
  return map[role] || 'Home'
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfoRaw = localStorage.getItem('userInfo')
  let role = null
  if (userInfoRaw) {
    try {
      role = JSON.parse(userInfoRaw).role
    } catch {
      role = null
    }
  }

  // 未登录访问需要登录的页面 -> 跳转登录
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login' })
    return
  }

  // 已登录访问登录页 -> 跳转对应角色首页
  if (to.name === 'Login' && token && role) {
    next({ name: getHomeRouteName(role) })
    return
  }

  // 已登录但访问了无权限的角色页面 -> 跳转对应角色首页
  if (to.meta.requiresAuth && to.meta.roles && !to.meta.roles.includes(role)) {
    next({ name: getHomeRouteName(role) })
    return
  }

  next()
})

export default router
