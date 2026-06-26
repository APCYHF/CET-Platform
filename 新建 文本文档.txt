<template>
  <div class="home-page">
    <!-- Hero Header -->
    <div class="hero">
      <div class="hero-top">
        <div class="greeting-area">
          <p class="greeting">{{ greeting }}，</p>
          <h2 class="username">{{ profile.nickname || profile.username || '同学' }} 👋</h2>
        </div>
        <button class="avatar-btn" @click="$router.push('/profile')">
          <img :src="profile.avatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=' + (profile.username || 'u')" alt="avatar" />
        </button>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProfile } from '@/api/user'
import { getWordStats } from '@/api/word'
import { getPapers } from '@/api/question'

const router = useRouter()
const profile = ref({})
const stats = ref({ totalWords: 0, mastered: 0, masteryRate: 0 })

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

onMounted(async () => {
  try { profile.value = await getProfile() } catch (e) {}
  try { stats.value = await getWordStats() } catch (e) {}
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

.hero-top { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 24px; }
.greeting { color: rgba(255,255,255,0.75); font-size: 14px; margin: 0 0 4px; }
.username { color: #fff; font-size: 22px; font-weight: 700; margin: 0; }

.avatar-btn {
  width: 44px; height: 44px; border-radius: 50%;
  border: 2.5px solid rgba(255,255,255,0.4);
  overflow: hidden; cursor: pointer; background: none; padding: 0;
  flex-shrink: 0;
}
.avatar-btn img { width: 100%; height: 100%; object-fit: cover; }

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
</style>