<template>
  <div class="profile-page">
    <!-- Header -->
    <div class="profile-hero">
      <div class="hero-bg"></div>
      <div class="avatar-wrap">
        <img :src="profile.avatar || 'https://api.dicebear.com/7.x/adventurer/svg?seed=' + (profile.username || 'u')" alt="avatar" />
        <div class="avatar-edit">📷</div>
      </div>
      <h2 class="profile-name">{{ profile.nickname || profile.username || '未设置' }}</h2>
      <p class="profile-school">{{ profile.school || '未设置学校' }} {{ profile.grade || '' }}</p>
      <div class="profile-tags">
        <span class="tag-item">🎯 {{ profile.targetScore || '—' }} 分目标</span>
        <span class="tag-item">⭐ {{ profile.points || 0 }} 积分</span>
      </div>
    </div>

    <!-- Stats Row -->
    <div class="stats-row">
      <div class="stats-card">
        <div class="sc-val">{{ profile.studyDuration || 0 }}</div>
        <div class="sc-label">学习时长(分)</div>
      </div>
      <div class="sc-divider"></div>
      <div class="stats-card">
        <div class="sc-val">{{ wordStats.totalWords || 0 }}</div>
        <div class="sc-label">已学单词</div>
      </div>
      <div class="sc-divider"></div>
      <div class="stats-card">
        <div class="sc-val">{{ wordStats.masteryRate || 0 }}%</div>
        <div class="sc-label">掌握率</div>
      </div>
    </div>

    <!-- Menu Groups -->
    <div class="menu-group">
      <div class="group-title">学习记录</div>
      <div class="menu-card">
        <div class="menu-row" v-for="item in studyMenu" :key="item.label" @click="$router.push(item.path)">
          <div class="mr-left">
            <span class="mr-icon" :style="{ background: item.bg }">{{ item.emoji }}</span>
            <span class="mr-label">{{ item.label }}</span>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M9 18L15 12L9 6" stroke="#9CA3AF" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
    </div>

    <div class="menu-group">
      <div class="group-title">账户设置</div>
      <div class="menu-card">
        <div class="menu-row" @click="showEditScore = true">
          <div class="mr-left">
            <span class="mr-icon" style="background:#EEF1FE">🎯</span>
            <span class="mr-label">修改目标分数</span>
          </div>
          <div style="display:flex;align-items:center;gap:8px">
            <span style="color:#9CA3AF;font-size:14px">{{ profile.targetScore || '未设置' }}</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M9 18L15 12L9 6" stroke="#9CA3AF" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <div class="logout-wrap">
      <button class="btn-logout" @click="handleLogout">退出登录</button>
    </div>

    <!-- Edit Score Dialog -->
    <van-dialog v-model:show="showEditScore" title="修改目标分数" show-cancel-button @confirm="updateTarget">
      <div style="padding: 20px 24px">
        <div class="dialog-label">目标分数（满分 710）</div>
        <input v-model="editScore" type="number" class="dialog-input" placeholder="例如: 500" />
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile } from '@/api/user'
import { getWordStats } from '@/api/word'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const profile = ref({})
const wordStats = ref({ totalWords: 0, masteryRate: 0 })
const showEditScore = ref(false)
const editScore = ref('')

const studyMenu = [
  { label: '成绩查询', emoji: '📊', bg: '#EEF1FE', path: '/scores' },
  { label: '生词本', emoji: '📖', bg: '#E8F5EE', path: '/vocabulary' },
  { label: '错题本', emoji: '❌', bg: '#FFF1F2', path: '/wrong' },
  { label: '我的订单', emoji: '📦', bg: '#FFF7ED', path: '/mall/orders' },
]

async function handleLogout() {
  userStore.logout()
  router.push('/login')
}

async function updateTarget() {
  if (!editScore.value) return
  try {
    await updateProfile({ targetScore: parseInt(editScore.value) })
    profile.value.targetScore = parseInt(editScore.value)
    showToast({ message: '更新成功', type: 'success' })
  } catch (e) {}
}

onMounted(async () => {
  try { profile.value = await getProfile() } catch (e) {}
  try { wordStats.value = await getWordStats() } catch (e) {}
})
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #F4F6FB; padding-bottom: 40px; }

.profile-hero {
  background: linear-gradient(145deg, #4F6EF7 0%, #6B87FF 100%);
  padding: 56px 24px 32px;
  text-align: center;
  border-radius: 0 0 32px 32px;
  position: relative;
}
.avatar-wrap {
  width: 80px; height: 80px; margin: 0 auto 16px; position: relative;
}
.avatar-wrap img {
  width: 80px; height: 80px; border-radius: 50%;
  border: 3px solid rgba(255,255,255,0.5);
  object-fit: cover;
}
.avatar-edit {
  position: absolute; bottom: 0; right: 0;
  width: 24px; height: 24px; border-radius: 50%;
  background: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  cursor: pointer;
}
.profile-name { color: #fff; font-size: 22px; font-weight: 700; margin: 0 0 6px; }
.profile-school { color: rgba(255,255,255,0.75); font-size: 14px; margin: 0 0 16px; }
.profile-tags { display: flex; gap: 8px; justify-content: center; }
.tag-item {
  background: rgba(255,255,255,0.2); color: #fff;
  font-size: 13px; font-weight: 500; padding: 6px 14px; border-radius: 20px;
}

.stats-row {
  margin: -1px 16px 0;
  background: #fff;
  border-radius: 16px;
  display: flex; align-items: center; justify-content: space-around;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(79,110,247,0.1);
  position: relative; z-index: 1;
}
.stats-card { text-align: center; flex: 1; }
.sc-val { font-size: 22px; font-weight: 800; color: #1A1D2E; }
.sc-label { font-size: 12px; color: #9CA3AF; margin-top: 4px; }
.sc-divider { width: 1px; height: 36px; background: #E5E7EB; }

.menu-group { padding: 20px 16px 0; }
.group-title { font-size: 13px; font-weight: 600; color: #9CA3AF; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 10px; }

.menu-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(79,110,247,0.06); }
.menu-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 16px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #F3F4F6;
}
.menu-row:last-child { border-bottom: none; }
.menu-row:active { background: #F9FAFB; }
.mr-left { display: flex; align-items: center; gap: 12px; }
.mr-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 18px; }
.mr-label { font-size: 15px; font-weight: 500; color: #1A1D2E; }

.logout-wrap { padding: 24px 16px 0; }
.btn-logout {
  width: 100%; height: 48px;
  background: #FFF1F2; color: #F05656;
  border: 1.5px solid #FEE2E2; border-radius: 14px;
  font-size: 16px; font-weight: 600; cursor: pointer;
  transition: background 0.15s;
}
.btn-logout:active { background: #FEE2E2; }

.dialog-label { font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 10px; }
.dialog-input {
  width: 100%; border: 1.5px solid #E5E7EB; border-radius: 10px;
  padding: 12px 14px; font-size: 16px; outline: none;
  color: #1A1D2E; box-sizing: border-box;
}
.dialog-input:focus { border-color: #4F6EF7; }
</style>