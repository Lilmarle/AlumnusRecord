import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post, put } from '@/api/index.js'

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

  // 添加毕业生相关状态
  const showAddGraduateModal = ref(false)
  const addGraduateForm = ref({
    studentId: null,
    graduateYear: null,
    destination: null,
    destinationDetail: '',
    certificateNo: ''
  })
  const teacherStudents = ref([])
  const addingGraduate = ref(false)

  // 修改毕业生相关状态
  const showEditGraduateModal = ref(false)
  const editGraduateForm = ref({
    id: null,
    graduateYear: null,
    destination: null,
    destinationDetail: '',
    certificateNo: ''
  })
  const editingGraduate = ref(false)

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
      const res = await get('/teacher-student/list', { teacherId })
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

  /** 加载教师关联的所有学生供添加毕业生选择 */
  async function loadTeacherAllStudents() {
    const teacherId = teacherInfo.value?.teacherId
    if (!teacherId) return
    try {
      const res = await get('/teacher-student/list', { teacherId })
      if (res.code === 1) {
        teacherStudents.value = res.data || []
      }
    } catch (e) {
      console.error('加载教师关联学生失败:', e)
    }
  }

  function openAddGraduateModal() {
    addGraduateForm.value = {
      studentId: null,
      graduateYear: null,
      destination: null,
      destinationDetail: '',
      certificateNo: ''
    }
    showAddGraduateModal.value = true
    loadTeacherAllStudents()
  }

  async function submitAddGraduate() {
    const f = addGraduateForm.value
    if (!f.studentId) {
      alert('请选择学生')
      return
    }
    if (!f.graduateYear) {
      alert('请输入毕业年份')
      return
    }
    if (!f.destination) {
      alert('请选择去向')
      return
    }
    addingGraduate.value = true
    try {
      const res = await post('/graduate/add', {
        studentId: f.studentId,
        graduateYear: f.graduateYear,
        destination: f.destination,
        destinationDetail: f.destinationDetail || null,
        certificateNo: f.certificateNo || null
      })
      if (res.code === 1) {
        showAddGraduateModal.value = false
        alert('添加毕业生成功')
        await loadGraduates()
      } else {
        alert(res.message || '添加失败')
      }
    } catch (e) {
      alert('添加失败: ' + e.message)
    } finally {
      addingGraduate.value = false
    }
  }

  function openEditGraduate(graduate) {
    editGraduateForm.value = {
      id: graduate.graduateId,
      graduateYear: graduate.graduateYear,
      destination: graduate.destination,
      destinationDetail: graduate.destinationDetail || '',
      certificateNo: graduate.certificateNo || ''
    }
    showEditGraduateModal.value = true
  }

  async function submitEditGraduate() {
    const f = editGraduateForm.value
    if (!f.graduateYear) { alert('请输入毕业年份'); return }
    if (!f.destination) { alert('请选择去向'); return }
    editingGraduate.value = true
    try {
      const res = await put('/graduate/update', {
        id: f.id,
        graduateYear: f.graduateYear,
        destination: f.destination,
        destinationDetail: f.destinationDetail || null,
        certificateNo: f.certificateNo || null
      })
      if (res.code === 1) {
        showEditGraduateModal.value = false
        alert('修改毕业生信息成功')
        await loadGraduates()
      } else {
        alert(res.message || '修改失败')
      }
    } catch (e) {
      alert('修改失败: ' + e.message)
    } finally {
      editingGraduate.value = false
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
    notSupervisor, switchTab, saveProfile, cancelEdit, logout,
    showAddGraduateModal, addGraduateForm, teacherStudents, addingGraduate,
    openAddGraduateModal, submitAddGraduate,
    showEditGraduateModal, editGraduateForm, editingGraduate,
    openEditGraduate, submitEditGraduate
  }
}
