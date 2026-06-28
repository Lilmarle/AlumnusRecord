import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/api'

/**
 * 根据角色获取对应的首页路由名称
 */
function getHomeRouteName(role) {
  const map = { 1: 'Home', 2: 'TeacherHome', 3: 'AdminHome' }
  return map[role] || 'Home'
}

export function useLogin() {
  const router = useRouter()
  const loading = ref(false)
  const errorMsg = ref('')

  const form = reactive({
    account: '',
    password: ''
  })

  async function doLogin() {
    errorMsg.value = ''

    if (!form.account.trim()) {
      errorMsg.value = '请输入用户名或手机号'
      return
    }
    if (!form.password.trim()) {
      errorMsg.value = '请输入密码'
      return
    }

    loading.value = true
    try {
      const result = await post('/user/login', {
        account: form.account,
        password: form.password
      })
      const { token, userId, username, phone, role } = result.data
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify({ userId, username, phone, role }))
      router.push({ name: getHomeRouteName(role) })
    } catch (err) {
      errorMsg.value = err.message || '登录失败'
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    errorMsg,
    form,
    doLogin
  }
}
