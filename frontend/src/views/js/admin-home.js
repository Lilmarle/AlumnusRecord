import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/api/index.js'

export function useAdminHome() {
  const router = useRouter()
  const userInfo = ref(null)
  const activeTab = ref('students')
  const students = ref([])
  const graduates = ref([])
  const editMode = ref(false)
  const editForm = ref({ name: '', gender: 0 })
  const showEditModal = ref(false)
  const editingStudent = ref(null)
  const editStudentForm = ref({
    id: null, studentNo: '', name: '', gender: 0,
    collegeId: null, majorId: null, classId: null,
    enrollDate: '', graduateDate: '', idCard: ''
  })
  const dictData = ref({ colleges: [], majors: [], classes: [] })
  const showGraduateDetail = ref(false)
  const graduateDetail = ref(null)

  const filteredMajors = computed(() => {
    if (!editStudentForm.value.collegeId) return dictData.value.majors || []
    return (dictData.value.majors || []).filter(m => m.collegeId === editStudentForm.value.collegeId)
  })

  /** 根据所选专业过滤班级 */
  const filteredClasses = computed(() => {
    if (!editStudentForm.value.majorId) return []
    return (dictData.value.classes || []).filter(c => c.majorId === editStudentForm.value.majorId)
  })

  /** 根据所选班级自动推导年级 */
  const selectedGrade = computed(() => {
    if (!editStudentForm.value.classId) return ''
    const cls = (dictData.value.classes || []).find(c => c.id === editStudentForm.value.classId)
    return cls ? cls.grade : ''
  })

  const loading = ref({ students: false, graduates: false })
  const roleMap = { 1: '学生', 2: '老师', 3: '管理员' }
  const genderMap = { 0: '未知', 1: '男', 2: '女' }
  const destinationMap = { 1: '就业', 2: '考公', 3: '考研' }

  onMounted(async () => {
    const raw = localStorage.getItem('userInfo')
    if (raw) {
      userInfo.value = JSON.parse(raw)
      editForm.value = { name: userInfo.value.username || '', gender: 0 }
    }
    if (activeTab.value === 'students') {
      await loadStudents()
    }
  })

  async function loadStudents() {
    loading.value.students = true
    students.value = []
    try {
      const res = await get('/student/list')
      if (res.code === 1) students.value = res.data || []
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
      const res = await get('/graduate/list')
      if (res.code === 1) graduates.value = res.data || []
    } catch (e) {
      console.error('获取毕业生列表失败:', e)
    } finally {
      loading.value.graduates = false
    }
  }

  async function switchTab(tab) {
    activeTab.value = tab
    if (tab === 'students' && students.value.length === 0) await loadStudents()
    else if (tab === 'graduates' && graduates.value.length === 0) await loadGraduates()
  }

  async function openEditStudent(student) {
    try {
      const dictRes = await get('/dict/all')
      if (dictRes.code === 1) dictData.value = dictRes.data || { colleges: [], majors: [], classes: [] }
    } catch (e) {
      console.error('获取字典数据失败:', e)
    }
    try {
      const infoRes = await get('/student/info', { userId: student.userId })
      if (infoRes.code === 1) {
        const d = infoRes.data
        editStudentForm.value = {
          id: d.studentId || student.studentId,
          studentNo: d.studentNo || '', name: d.name || '',
          gender: d.gender ?? 0, collegeId: d.collegeId || null,
          majorId: d.majorId || null, classId: d.classId || null,
          enrollDate: d.enrollDate || '',
          graduateDate: d.graduateDate || '', idCard: d.idCard || ''
        }
      } else {
        setFormFromStudent(student)
      }
    } catch (e) {
      console.error('获取学生详情失败:', e)
      setFormFromStudent(student)
    }
    editingStudent.value = student
    showEditModal.value = true
  }

  function setFormFromStudent(student) {
    editStudentForm.value = {
      id: student.studentId, studentNo: student.studentNo || '',
      name: student.name || '', gender: student.gender ?? 0,
      collegeId: null, majorId: null, classId: null,
      enrollDate: '', graduateDate: '', idCard: ''
    }
  }

  async function saveStudent() {
    try {
      const f = editStudentForm.value
      const payload = {
        id: f.id, studentNo: f.studentNo || null, name: f.name || null,
        gender: f.gender, collegeId: f.collegeId || null,
        majorId: f.majorId || null, classId: f.classId || null,
        enrollDate: f.enrollDate || null, graduateDate: f.graduateDate || null,
        idCard: f.idCard || null
      }
      const res = await post('/student/update', payload)
      if (res.code === 1) {
        showEditModal.value = false
        alert('学生信息修改成功')
        await loadStudents()
      } else {
        alert(res.message || '修改失败')
      }
    } catch (e) {
      alert('保存失败: ' + e.message)
    }
  }

  async function saveProfile() {
    try {
      const res = await post('/user/profile/update', {
        userId: userInfo.value.id, name: editForm.value.name, gender: editForm.value.gender
      })
      if (res.code === 1) {
        editMode.value = false
        const updated = { ...userInfo.value, username: editForm.value.name }
        localStorage.setItem('userInfo', JSON.stringify(updated))
        userInfo.value = updated
        alert('个人信息修改成功')
      } else {
        alert(res.message || '修改失败')
      }
    } catch (e) {
      alert('保存失败: ' + e.message)
    }
  }

  function openGraduateDetail(row) {
    graduateDetail.value = row
    showGraduateDetail.value = true
  }

  function closeGraduateDetail() {
    showGraduateDetail.value = false
    graduateDetail.value = null
  }

  function cancelEdit() {
    editMode.value = false
    if (userInfo.value) editForm.value = { name: userInfo.value.username || '', gender: 0 }
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/')
  }

  return {
    userInfo, activeTab, students, graduates, editMode, editForm,
    showEditModal, editingStudent, editStudentForm, dictData,
    filteredMajors, filteredClasses, selectedGrade,
    loading, roleMap, genderMap, destinationMap,
    showGraduateDetail, graduateDetail,
    switchTab, openEditStudent, saveStudent, saveProfile, cancelEdit, logout,
    openGraduateDetail, closeGraduateDetail
  }
}
