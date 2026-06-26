<template>
  <div class="home-page">
    <!-- Hero Header -->
    <div class="hero">
      <div class="hero-top">
        <div class="hero-top-row">
          <div class="greeting-area">
            <p class="greeting">{{ greeting }}，</p>
            <h2 class="username">{{ profile.nickname || profile.username || '同学' }} 👋</h2>
          </div>
          <button class="avatar-btn" @click="$router.push('/profile')">
            <img
              :src="profile.avatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=' + (profile.username || 'u')"
              alt="avatar"
            />
          </button>
        </div>

        <!-- 倒计时条 -->
        <div class="countdown-bar" @click="openSetting">
          <template v-if="!isExamSet">
            <span class="countdown-placeholder">
              <span class="countdown-icon">📌</span>
              点击设置考试时间
            </span>
          </template>
          <template v-else-if="isExamExpired">
            <span class="countdown-expired">
              <span class="countdown-icon">⏰</span>
              考试已结束，请重新设置
            </span>
          </template>
          <template v-else>
            <span class="countdown-display">
              距离
              <span class="exam-type-badge">{{ examTypeLabel }}</span>
              考试还有
              <span class="countdown-num">{{ countdown.days }}</span><span class="countdown-unit">天</span>
              <span class="countdown-num">{{ countdown.hours }}</span><span class="countdown-unit">时</span>
              <span class="countdown-num">{{ countdown.minutes }}</span><span class="countdown-unit">分</span>
              <span class="countdown-num countdown-seconds">{{ countdown.seconds }}</span><span class="countdown-unit">秒</span>
            </span>
          </template>
        </div>
      </div>

      <!-- Study streak / target -->
      <div class="hero-stats">
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalWords }}</div>
          <div class="stat-label">已学单词</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.masteryRate }}<span class="stat-unit">%</span></div>
          <div class="stat-label">掌握率</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-value">{{ profile.studyDuration || 0 }}</div>
          <div class="stat-label">学习分钟</div>
        </div>
      </div>

      <!-- Target score bar -->
      <div v-if="profile.targetScore" class="target-bar">
        <div class="target-info">
          <span>目标 {{ profile.targetScore }} 分</span>
          <span class="target-pct">{{ targetProgress }}%</span>
        </div>
        <div class="target-track">
          <div class="target-fill" :style="{ width: targetProgress + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- Banner Swipe -->
    <div class="banner-section">
      <van-swipe :autoplay="4000" indicator-color="#4F6EF7" :show-indicators="true">
        <van-swipe-item v-for="(b, i) in banners" :key="i">
          <div class="banner-card" :style="{ background: b.bg }">
            <div class="banner-text">
              <div class="banner-tag">{{ b.tag }}</div>
              <h3>{{ b.title }}</h3>
              <p>{{ b.sub }}</p>
            </div>
            <div class="banner-icon">{{ b.emoji }}</div>
          </div>
        </van-swipe-item>
      </van-swipe>
    </div>

    <!-- Quick Menu -->
    <div class="section">
      <div class="section-title">快速入口</div>
      <div class="quick-grid">
        <div v-for="m in menus" :key="m.label" class="menu-item" @click="m.action()">
          <div class="menu-icon" :style="{ background: m.bg }">
            <span>{{ m.emoji }}</span>
          </div>
          <span class="menu-label">{{ m.label }}</span>
        </div>
      </div>
    </div>

    <!-- Daily Plan -->
    <div class="section">
      <div class="section-title">今日计划</div>
      <div class="plan-cards">
        <div class="plan-card" @click="$router.push('/study')">
          <div class="plan-icon" style="background:#EEF1FE">📖</div>
          <div class="plan-info">
            <div class="plan-name">背单词</div>
            <div class="plan-progress">今日目标 20 词</div>
          </div>
          <div class="plan-arrow">→</div>
        </div>
        <div class="plan-card" @click="$router.push('/practice/reading')">
          <div class="plan-icon" style="background:#E8F5EE">✍️</div>
          <div class="plan-info">
            <div class="plan-name">阅读刷题</div>
            <div class="plan-progress">每日 5 题</div>
          </div>
          <div class="plan-arrow">→</div>
        </div>
      </div>
    </div>

    <div style="height: 80px"></div>

    <!-- 设置考试时间弹出层 -->
    <van-popup v-model:show="showPopup" position="bottom" round class="exam-popup" :style="{ height: '60vh' }">
      <div class="popup-container">
        <div class="popup-header">
          <span class="popup-cancel" @click="showPopup = false">取消</span>
          <span class="popup-title">设置考试时间</span>
          <span class="popup-confirm" @click="saveExam">确认</span>
        </div>
        <div class="popup-body">
          <div class="exam-type-group">
            <div class="exam-type-label">选择考试</div>
            <div class="exam-type-options">
              <div
                class="exam-type-option"
                :class="{ active: tempExamType === 'cet4' }"
                @click="tempExamType = 'cet4'"
              >
                📘 英语四级
              </div>
              <div
                class="exam-type-option"
                :class="{ active: tempExamType === 'cet6' }"
                @click="tempExamType = 'cet6'"
              >
                📗 英语六级
              </div>
            </div>
          </div>
          <div class="exam-date-group">
            <div class="exam-date-label">选择考试日期</div>
            <div class="picker-wrapper">
              <van-date-picker
                v-model="tempExamDate"
                :min-date="minDate"
                :max-date="maxDate"
                :formatter="formatter"
              />
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getProfile } from '@/api/user'
import { getWordStats } from '@/api/word'
import { getPapers } from '@/api/question'
import { showToast } from 'vant'

const router = useRouter()
const profile = ref({})
const stats = ref({ totalWords: 0, mastered: 0, masteryRate: 0 })

// ============ 倒计时相关 ============
const STORAGE_KEY = 'exam_settings'
const examType = ref(null) // 'cet4' | 'cet6' | null
const examDate = ref(null) // Date 对象
const countdown = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 })
let timer = null

// 弹出层相关
const showPopup = ref(false)
const tempExamType = ref('cet4')

// Vant 4 DatePicker v-model 使用数组格式 [year, month, day]
function dateToArr(d) {
  return [String(d.getFullYear()), String(d.getMonth() + 1), String(d.getDate())]
}
function arrToDate(arr) {
  return new Date(+arr[0], +arr[1] - 1, +arr[2])
}
const tempExamDate = ref(dateToArr(new Date()))

// 日期限制
const minDate = new Date()
const maxDate = new Date()
maxDate.setFullYear(maxDate.getFullYear() + 3)

// 计算属性
const isExamSet = computed(() => examType.value && examDate.value)
const isExamExpired = computed(() => {
  if (!isExamSet.value) return false
  return new Date() > new Date(examDate.value)
})

const examTypeLabel = computed(() => {
  if (examType.value === 'cet4') return '四级'
  if (examType.value === 'cet6') return '六级'
  return ''
})

// 日期格式化（用于显示）
function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

// 更新倒计时
function updateCountdown() {
  if (!isExamSet.value || isExamExpired.value) {
    return
  }
  const now = new Date()
  const target = new Date(examDate.value)
  const diff = target.getTime() - now.getTime()

  if (diff <= 0) {
    countdown.value = { days: 0, hours: 0, minutes: 0, seconds: 0 }
    return
  }

  const seconds = Math.floor(diff / 1000)
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  countdown.value = { days, hours, minutes, seconds: secs }
}

// 启动/停止定时器
function startTimer() {
  stopTimer()
  if (isExamSet.value && !isExamExpired.value) {
    updateCountdown()
    timer = setInterval(updateCountdown, 1000)
  }
}

function stopTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

// 加载存储的数据
function loadExamData() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    if (data.type && data.date) {
      examType.value = data.type
      examDate.value = new Date(data.date)
      startTimer()
    }
  } catch (e) {
    console.warn('读取考试设置失败', e)
  }
}

// 保存数据到存储
function saveExamData() {
  try {
    const data = {
      type: examType.value,
      date: examDate.value ? examDate.value.toISOString() : null,
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  } catch (e) {
    console.warn('保存考试设置失败', e)
  }
}

// 打开设置面板
function openSetting() {
  tempExamType.value = examType.value || 'cet4'
  // 如果已有考试日期且未过期，则沿用；否则默认今天
  if (examDate.value && new Date(examDate.value) >= new Date()) {
    tempExamDate.value = dateToArr(new Date(examDate.value))
  } else {
    tempExamDate.value = dateToArr(new Date())
  }
  showPopup.value = true
}

// 保存设置
function saveExam() {
  if (!tempExamType.value) {
    showToast('请选择考试类型')
    return
  }
  if (!tempExamDate.value || tempExamDate.value.length < 3) {
    showToast('请选择考试日期')
    return
  }
  const selectedDate = arrToDate(tempExamDate.value)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  if (selectedDate < today) {
    showToast('考试日期不能早于今天')
    return
  }

  examType.value = tempExamType.value
  examDate.value = selectedDate
  saveExamData()
  showPopup.value = false
  startTimer() // 重新启动定时器
  showToast(`已设置${examTypeLabel.value}考试日期为 ${formatDate(selectedDate)}`)
}

// 日期格式化器（显示中文 — Vant 4 API）
function formatter(type, option) {
  if (type === 'year') option.text += '年'
  if (type === 'month') option.text += '月'
  if (type === 'day') option.text += '日'
  return option
}

// ============ 原有逻辑 ============
const now = new Date().getHours()
const greeting = now < 12 ? '早上好' : now < 18 ? '下午好' : '晚上好'

const targetProgress = computed(() => {
  if (!profile.value.targetScore) return 0
  return Math.min(100, Math.round((stats.value.masteryRate || 0)))
})

const banners = [
  { tag: '四六级', title: '真题模拟考试', sub: '历年真题，在线模拟考场', emoji: '📝', bg: 'linear-gradient(135deg, #4F6EF7 0%, #6B87FF 100%)' },
  { tag: '高效学习', title: '智能单词记忆', sub: '艾宾浩斯曲线，科学复习', emoji: '🧠', bg: 'linear-gradient(135deg, #06C073 0%, #34D399 100%)' },
  { tag: '新功能', title: 'AI批改作文', sub: '智能评分，精准提升写作', emoji: '✨', bg: 'linear-gradient(135deg, #F05656 0%, #FB7185 100%)' },
]

const menus = [
  { label: '阅读刷题', emoji: '📚', bg: '#EEF1FE', action: () => router.push('/practice/reading') },
  { label: '听力练习', emoji: '🎧', bg: '#E8F5EE', action: () => router.push('/practice/listening') },
  { label: '背单词', emoji: '📖', bg: '#FFF7ED', action: () => router.push('/study') },
  { label: '写作翻译', emoji: '✍️', bg: '#FEF2F2', action: () => router.push('/composition') },
  { label: '成绩查询', emoji: '📊', bg: '#F0FDF4', action: () => router.push('/scores') },
  { label: '错题本', emoji: '❌', bg: '#FFF1F2', action: () => router.push('/wrong') },
  { label: '资料商城', emoji: '🛒', bg: '#F0F9FF', action: () => router.push('/mall') },
  { label: '整套真题', emoji: '🏆', bg: '#FFFBEB', action: goPaper },
]

async function goPaper() {
  try {
    const papers = await getPapers()
    if (papers && papers.length > 0) router.push('/paper/' + papers[0].id)
    else router.push('/practice/reading')
  } catch (e) { router.push('/practice/reading') }
}

// ============ 生命周期 ============
onMounted(async () => {
  // 加载原有数据
  try { profile.value = await getProfile() } catch (e) {}
  try { stats.value = await getWordStats() } catch (e) {}

  // 加载考试设置
  loadExamData()
})

onBeforeUnmount(() => {
  stopTimer()
})
</script>

<style scoped>
.home-page { background: #F4F6FB; min-height: 100vh; }

/* Hero */
.hero {
  background: linear-gradient(145deg, #4F6EF7 0%, #5B7BFF 60%, #7B97FF 100%);
  padding: 56px 20px 28px;
  border-radius: 0 0 28px 28px;
}

.hero-top {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}
.hero-top-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.greeting { color: rgba(255,255,255,0.75); font-size: 14px; margin: 0 0 4px; }
.username { color: #fff; font-size: 22px; font-weight: 700; margin: 0; }

.avatar-btn {
  width: 44px; height: 44px; border-radius: 50%;
  border: 2.5px solid rgba(255,255,255,0.4);
  overflow: hidden; cursor: pointer; background: none; padding: 0;
  flex-shrink: 0;
}
.avatar-btn img { width: 100%; height: 100%; object-fit: cover; }

/* ===== 倒计时条 ===== */
.countdown-bar {
  width: 100%;
  padding: 10px 16px;
  border-radius: 14px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-sizing: border-box;
  user-select: none;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.countdown-bar:active {
  transform: scale(0.98);
  background: rgba(255, 255, 255, 0.15);
}

.countdown-placeholder {
  color: rgba(255, 255, 255, 0.75);
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.countdown-icon {
  font-size: 16px;
}

.countdown-expired {
  color: rgba(255, 200, 200, 0.9);
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.countdown-display {
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 0.3px;
  display: flex;
  align-items: center;
  gap: 2px;
  flex-wrap: wrap;
  justify-content: center;
}

.exam-type-badge {
  display: inline-block;
  background: rgba(255, 215, 0, 0.25);
  color: #FFD700;
  font-size: 13px;
  font-weight: 700;
  padding: 0 10px;
  border-radius: 20px;
  line-height: 24px;
  margin: 0 2px;
}

.countdown-num {
  display: inline-block;
  font-size: 18px;
  font-weight: 800;
  color: #FFD700;
  min-width: 28px;
  text-align: center;
  background: rgba(255, 215, 0, 0.12);
  border-radius: 6px;
  padding: 0 4px;
  line-height: 30px;
  font-variant-numeric: tabular-nums;
}

.countdown-seconds {
  color: #7DD3FC;
  background: rgba(125, 211, 252, 0.12);
}

.countdown-unit {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 400;
  margin-right: 2px;
}

/* ===== Hero Stats ===== */
.hero-stats {
  display: flex; align-items: center; justify-content: space-around;
  background: rgba(255,255,255,0.15);
  border-radius: 16px; padding: 16px;
  margin-bottom: 16px;
  backdrop-filter: blur(10px);
}
.stat-item { text-align: center; }
.stat-value { font-size: 22px; font-weight: 800; color: #fff; }
.stat-unit { font-size: 14px; font-weight: 500; }
.stat-label { font-size: 12px; color: rgba(255,255,255,0.7); margin-top: 2px; }
.stat-divider { width: 1px; height: 32px; background: rgba(255,255,255,0.25); }

.target-bar { background: rgba(255,255,255,0.15); border-radius: 12px; padding: 12px 16px; }
.target-info { display: flex; justify-content: space-between; color: rgba(255,255,255,0.9); font-size: 12px; margin-bottom: 8px; }
.target-pct { font-weight: 700; }
.target-track { height: 6px; background: rgba(255,255,255,0.25); border-radius: 3px; overflow: hidden; }
.target-fill { height: 100%; background: #fff; border-radius: 3px; transition: width 0.6s; }

/* Banner */
.banner-section { padding: 20px 16px 0; }
.banner-card {
  height: 120px; border-radius: 16px; padding: 20px;
  display: flex; align-items: center; justify-content: space-between;
}
.banner-tag { display: inline-block; background: rgba(255,255,255,0.25); color: #fff; font-size: 11px; padding: 2px 8px; border-radius: 20px; margin-bottom: 6px; }
.banner-text h3 { color: #fff; font-size: 18px; font-weight: 700; margin: 0 0 4px; }
.banner-text p { color: rgba(255,255,255,0.8); font-size: 12px; margin: 0; }
.banner-icon { font-size: 48px; opacity: 0.9; }

/* Section */
.section { padding: 20px 16px 0; }
.section-title { font-size: 16px; font-weight: 700; color: #1A1D2E; margin-bottom: 14px; }

/* Quick Grid */
.quick-grid {
  display: grid; grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.menu-item {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  cursor: pointer;
}
.menu-icon {
  width: 56px; height: 56px; border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: transform 0.15s;
}
.menu-item:active .menu-icon { transform: scale(0.92); }
.menu-label { font-size: 12px; color: #374151; font-weight: 500; text-align: center; }

/* Plan Cards */
.plan-cards { display: flex; flex-direction: column; gap: 10px; }
.plan-card {
  background: #fff; border-radius: 14px; padding: 16px;
  display: flex; align-items: center; gap: 14px;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06);
  cursor: pointer;
  transition: transform 0.15s;
}
.plan-card:active { transform: scale(0.98); }
.plan-icon { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 22px; flex-shrink: 0; }
.plan-info { flex: 1; }
.plan-name { font-size: 15px; font-weight: 600; color: #1A1D2E; }
.plan-progress { font-size: 12px; color: #9CA3AF; margin-top: 2px; }
.plan-arrow { color: #9CA3AF; font-size: 18px; }

/* ===== 弹出层样式 ===== */
.exam-popup {
  border-radius: 20px 20px 0 0 !important;
  overflow: hidden;
}
.popup-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
}
.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 12px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}
.popup-cancel {
  color: #999;
  font-size: 15px;
  cursor: pointer;
  padding: 4px 8px;
}
.popup-title {
  font-size: 17px;
  font-weight: 700;
  color: #1A1D2E;
}
.popup-confirm {
  color: #4F6EF7;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  padding: 4px 8px;
}
.popup-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 20px 20px;
  overflow: hidden;
}

.exam-type-group {
  flex-shrink: 0;
  margin-bottom: 16px;
}
.exam-type-label {
  font-size: 14px;
  font-weight: 600;
  color: #1A1D2E;
  margin-bottom: 10px;
}
.exam-type-options {
  display: flex;
  gap: 12px;
}
.exam-type-option {
  flex: 1;
  padding: 12px 0;
  text-align: center;
  border-radius: 12px;
  border: 2px solid #e8ecf1;
  font-size: 15px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.25s ease;
  background: #f8f9fb;
}
.exam-type-option:active {
  transform: scale(0.96);
}
.exam-type-option.active {
  border-color: #4F6EF7;
  background: rgba(79, 110, 247, 0.08);
  color: #4F6EF7;
  box-shadow: 0 0 0 4px rgba(79, 110, 247, 0.08);
}

.exam-date-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 防止flex溢出 */
}
.exam-date-label {
  font-size: 14px;
  font-weight: 600;
  color: #1A1D2E;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.picker-wrapper {
  flex: 1;
  min-height: 180px;
  overflow: hidden;
  position: relative;
}
/* 修复 van-date-picker 在弹出层中的显示 - Vant 4 兼容 */
.exam-popup :deep(.van-picker) {
  display: flex !important;
  width: 100% !important;
  padding: 0 16.67% !important;
  box-sizing: border-box !important;
}

.exam-popup :deep(.van-picker-column) {
  flex: 1 1 0 !important;
  min-width: 0 !important;
  width: auto !important;
}

.exam-popup :deep(.van-picker-column__item) {
  padding: 10px 0 !important;
  font-size: 16px !important;
  text-align: center !important;
  justify-content: center !important;
}

.exam-popup :deep(.van-picker-column__item--selected) {
  color: #4F6EF7 !important;
  font-weight: 600 !important;
}

.exam-popup :deep(.van-picker__toolbar) {
  display: none !important;
}

.exam-popup :deep(.van-picker__columns) {
  display: flex !important;
  width: 100% !important;
}
</style>