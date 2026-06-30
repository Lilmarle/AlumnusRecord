import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/api/index.js'

export function useTeacherHome() {
  const router = useRouter()
  const userInfo = ref(null)
  const activeTab = ref('students')
  const teacherInfo = ref(null)
  const students = ref([])
  const graduates = ref([])
  const editMode = ref(false)
  const editForm = ref({
    name: '',
    gender: null,
    phone: '',
    title: '',
    employeeNo: ''
  })
  const notSupervisor = ref(false)
  const loading = ref({
    students: false,
    graduates: false,
    profile: false
  })

  const roleMap = { 1: '学生', 2: '老师', 3: '管理员' }
  const destinationMap = { 1: '就业', 2: '考公', 3: '考研' }
  const genderMap = { 0: '未知', 1: '男', 2: '女' }

  onMounted(async () => {
    const raw = localStorage.getItem('userInfo')
    if (raw) {
      userInfo.value = JSON.parse(raw)
    }
    await loadTeacherInfo()
    if (activeTab.value === 'students') {
      await loadStudents()
    }
  })

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

  async function loadStudents() {
    loading.value.students = true
    students.value = []
    notSupervisor.value = false
    try {
      // 直接用 teacherId 查指导学生，无需依赖 supervisor 表是否存在
      const teacherId = teacherInfo.value?.teacherId
      if (!teacherId) {
        loading.value.students = false
        return
      }
      const res = await get('/teacher-student/list', { teacherId, type: 2 })
      if (res.code === 1) {
        // 后端返回的 gender 为数字，转换为显示用的 genderName
        students.value = (res.data || []).map(item => ({
          ...item,
          genderName: genderMap[item.gender] || '未知',
          userStatus: item.userStatus ?? 1,
          statusName: item.statusName || '在校'
        }))
      }
    } catch (e) {
      console.error('获取学生列表失败:', e)
    } finally {
      loading.value.students = false
    }
  }

  async function loadGraduates() {
    loading.value.graduates = true
    graduates.value = []
    try {
      const res = await get('/teacher/graduates')
      if (res.code === 1) {
        graduates.value = res.data || []
      }
    } catch (e) {
      console.error('获取毕业生列表失败:', e)
    } finally {
      loading.value.graduates = false
    }
  }

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

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/')
  }

  return {
    userInfo, activeTab, teacherInfo, students, graduates,
    editMode, editForm, loading, roleMap, destinationMap, genderMap,
    notSupervisor, switchTab, saveProfile, cancelEdit, logout
  }
}
