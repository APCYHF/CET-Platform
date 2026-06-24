<template>
  <div class="scores-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>成绩查询</h2>
      <button class="add-btn" @click="showForm = true">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <line x1="12" y1="5" x2="12" y2="19" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
          <line x1="5" y1="12" x2="19" y2="12" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>

    <!-- Summary Card -->
    <div v-if="bestScore" class="best-card">
      <div class="best-label">最佳成绩</div>
      <div class="best-score">{{ bestScore.totalScore }}</div>
      <div class="best-meta">{{ bestScore.examType }} · {{ bestScore.examDate }}</div>
      <div class="best-breakdown">
        <div class="bb-item">
          <div class="bb-val">{{ bestScore.listenScore || '—' }}</div>
          <div class="bb-label">听力</div>
        </div>
        <div class="bb-item">
          <div class="bb-val">{{ bestScore.readScore || '—' }}</div>
          <div class="bb-label">阅读</div>
        </div>
        <div class="bb-item">
          <div class="bb-val">{{ bestScore.writeScore || '—' }}</div>
          <div class="bb-label">写译</div>
        </div>
      </div>
    </div>

    <!-- History List -->
    <div class="section-label">历史记录 ({{ scores.length }} 条)</div>
    <div class="scores-list">
      <div v-for="s in scores" :key="s.id" class="score-card">
        <div class="sc-left">
          <div class="exam-badge" :class="s.examType === 'CET6' ? 'badge-six' : 'badge-four'">
            {{ s.examType }}
          </div>
          <div class="score-info">
            <div class="score-date">{{ s.examDate }}</div>
            <div class="score-breakdown">
              听力 {{ s.listenScore || '—' }} / 阅读 {{ s.readScore || '—' }} / 写译 {{ s.writeScore || '—' }}
            </div>
          </div>
        </div>
        <div class="score-total" :class="passClass(s)">{{ s.totalScore }}</div>
      </div>
      <div v-if="!scores.length" class="empty-state">
        <div class="empty-icon">📊</div>
        <p>暂无成绩记录</p>
        <p class="empty-sub">点击右上角 + 录入成绩</p>
      </div>
    </div>

    <!-- Add Score Dialog -->
    <van-dialog v-model:show="showForm" title="录入成绩" show-cancel-button @confirm="handleAdd" :confirm-button-loading="adding">
      <div class="dialog-form">
        <div class="df-row">
          <label>考试类型</label>
          <div class="type-toggle">
            <button :class="{ active: form.examType === 'CET4' }" @click="form.examType = 'CET4'">CET-4</button>
            <button :class="{ active: form.examType === 'CET6' }" @click="form.examType = 'CET6'">CET-6</button>
          </div>
        </div>
        <div class="df-row">
          <label>考试日期</label>
          <input v-model="form.examDate" type="date" class="df-input" />
        </div>
        <div class="df-row">
          <label>总分</label>
          <input v-model.number="form.totalScore" type="number" class="df-input" placeholder="710" />
        </div>
        <div class="df-grid">
          <div>
            <label>听力</label>
            <input v-model.number="form.listenScore" type="number" class="df-input sm" placeholder="0" />
          </div>
          <div>
            <label>阅读</label>
            <input v-model.number="form.readScore" type="number" class="df-input sm" placeholder="0" />
          </div>
          <div>
            <label>写译</label>
            <input v-model.number="form.writeScore" type="number" class="df-input sm" placeholder="0" />
          </div>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getMyScores, addScore } from '@/api/score'
import { showToast } from 'vant'

const scores = ref([])
const showForm = ref(false)
const adding = ref(false)
const form = reactive({ examType: 'CET4', examDate: '', totalScore: 0, listenScore: 0, readScore: 0, writeScore: 0 })

const bestScore = computed(() => {
  if (!scores.value.length) return null
  return scores.value.reduce((a, b) => (a.totalScore > b.totalScore ? a : b))
})

function passClass(s) {
  const pass = s.examType === 'CET4' ? 425 : 425
  return s.totalScore >= pass ? 'score-pass' : 'score-fail'
}

async function handleAdd() {
  adding.value = true
  try {
    await addScore(form)
    showToast({ message: '录入成功 🎉', type: 'success' })
    showForm.value = false
    scores.value = await getMyScores()
  } catch (e) {}
  adding.value = false
}

onMounted(async () => {
  try { scores.value = await getMyScores() } catch (e) {}
})
</script>

<style scoped>
.scores-page { min-height: 100vh; background: #F4F6FB; padding-bottom: 40px; }

.page-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn, .add-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2);
  color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.page-header h2 { flex: 1; color: #fff; font-size: 18px; font-weight: 700; margin: 0; text-align: center; }

.best-card {
  margin: 20px 16px 0;
  background: linear-gradient(135deg, #1A1D2E 0%, #2D3348 100%);
  border-radius: 20px; padding: 28px 24px; text-align: center;
  box-shadow: 0 8px 24px rgba(26,29,46,0.25);
}
.best-label { color: rgba(255,255,255,0.6); font-size: 13px; margin-bottom: 8px; }
.best-score { font-size: 56px; font-weight: 900; color: #4F6EF7; line-height: 1; margin-bottom: 6px; }
.best-meta { color: rgba(255,255,255,0.5); font-size: 13px; margin-bottom: 20px; }
.best-breakdown { display: flex; justify-content: space-around; }
.bb-item { text-align: center; }
.bb-val { font-size: 20px; font-weight: 700; color: #fff; }
.bb-label { font-size: 12px; color: rgba(255,255,255,0.5); margin-top: 4px; }

.section-label { font-size: 13px; font-weight: 600; color: #9CA3AF; padding: 20px 20px 10px; text-transform: uppercase; }

.scores-list { padding: 0 16px; display: flex; flex-direction: column; gap: 10px; }
.score-card {
  background: #fff; border-radius: 14px; padding: 16px;
  display: flex; align-items: center; justify-content: space-between;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06);
}
.sc-left { display: flex; align-items: center; gap: 12px; }
.exam-badge {
  font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 8px;
  white-space: nowrap;
}
.badge-four { background: #EEF1FE; color: #4F6EF7; }
.badge-six { background: #F0FDF4; color: #06C073; }
.score-date { font-size: 14px; font-weight: 600; color: #1A1D2E; }
.score-breakdown { font-size: 12px; color: #9CA3AF; margin-top: 2px; }
.score-total { font-size: 24px; font-weight: 800; }
.score-pass { color: #06C073; }
.score-fail { color: #F05656; }

.empty-state { padding: 48px 0; text-align: center; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-state p { color: #6B7280; margin: 0 0 4px; font-size: 16px; }
.empty-sub { color: #9CA3AF !important; font-size: 13px !important; }

.dialog-form { padding: 20px 24px; }
.df-row { margin-bottom: 16px; }
.df-row label { font-size: 13px; font-weight: 600; color: #374151; display: block; margin-bottom: 8px; }
.df-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.df-input {
  width: 100%; border: 1.5px solid #E5E7EB; border-radius: 10px;
  padding: 10px 12px; font-size: 15px; color: #1A1D2E;
  outline: none; box-sizing: border-box;
}
.df-input.sm { padding: 8px 10px; font-size: 14px; }
.df-input:focus { border-color: #4F6EF7; }
.type-toggle { display: flex; gap: 8px; }
.type-toggle button {
  flex: 1; padding: 8px; border-radius: 10px;
  border: 1.5px solid #E5E7EB; background: #F9FAFB;
  font-size: 14px; font-weight: 600; cursor: pointer;
  color: #6B7280; transition: all 0.2s;
}
.type-toggle button.active { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }
</style>