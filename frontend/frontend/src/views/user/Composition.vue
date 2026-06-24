<template>
  <div class="composition-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>写作翻译</h2>
      <button class="add-btn" @click="showSubmit = true">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <line x1="12" y1="5" x2="12" y2="19" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
          <line x1="5" y1="12" x2="19" y2="12" stroke="white" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>

    <!-- Banner -->
    <div class="comp-banner">
      <div class="cb-text">
        <h3>AI 智能批改</h3>
        <p>提交作文，获取 AI 评分与建议</p>
      </div>
      <div class="cb-icon">🤖</div>
    </div>

    <!-- Tabs -->
    <div class="type-tabs">
      <button :class="['type-btn', { active: typeFilter === 'ALL' }]" @click="typeFilter = 'ALL'">全部</button>
      <button :class="['type-btn', { active: typeFilter === 'WRITING' }]" @click="typeFilter = 'WRITING'">作文</button>
      <button :class="['type-btn', { active: typeFilter === 'TRANSLATION' }]" @click="typeFilter = 'TRANSLATION'">翻译</button>
    </div>

    <!-- List -->
    <div class="comp-list">
      <div v-for="c in filteredList" :key="c.id" class="comp-item">
        <div class="ci-header">
          <span class="ci-type-badge" :class="c.type === 'WRITING' ? 'writing' : 'translation'">
            {{ c.type === 'WRITING' ? '作文' : '翻译' }}
          </span>
          <span v-if="c.aiScore" class="ci-score">{{ c.aiScore }} 分</span>
          <span v-else class="ci-pending">待批改</span>
        </div>
        <div v-if="c.title" class="ci-title">{{ c.title }}</div>
        <div class="ci-content">{{ c.content }}</div>
        <div v-if="c.aiFeedback" class="ci-feedback">
          <div class="feedback-label">💬 AI 批改意见</div>
          <div class="feedback-text">{{ c.aiFeedback }}</div>
        </div>
      </div>
      <div v-if="!filteredList.length" class="empty-state">
        <div class="empty-icon">✍️</div>
        <p>还没有提交记录</p>
        <p class="empty-sub">点击右上角 + 提交作文或翻译</p>
      </div>
    </div>

    <!-- Submit Dialog -->
    <van-dialog v-model:show="showSubmit" title="提交作文/翻译" show-cancel-button @confirm="handleSubmit" :confirm-button-loading="submitting">
      <div class="dialog-form">
        <div class="df-row">
          <label>提交类型</label>
          <div class="type-toggle">
            <button :class="{ active: form.type === 'WRITING' }" @click="form.type = 'WRITING'">作文</button>
            <button :class="{ active: form.type === 'TRANSLATION' }" @click="form.type = 'TRANSLATION'">翻译</button>
          </div>
        </div>
        <div class="df-row">
          <label>题目（选填）</label>
          <input v-model="form.title" class="df-input" placeholder="例如：大学生创业" />
        </div>
        <div class="df-row">
          <label>内容</label>
          <textarea v-model="form.content" class="df-textarea" placeholder="输入作文或翻译内容..." rows="8"></textarea>
          <div class="word-count">{{ form.content.length }} 字</div>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { submitComposition, getMyCompositions } from '@/api/composition'
import { showToast } from 'vant'

const list = ref([])
const showSubmit = ref(false)
const submitting = ref(false)
const typeFilter = ref('ALL')
const form = reactive({ type: 'WRITING', title: '', content: '' })

const filteredList = computed(() => {
  if (typeFilter.value === 'ALL') return list.value
  return list.value.filter(c => c.type === typeFilter.value)
})

async function handleSubmit() {
  if (!form.content.trim()) {
    showToast('请输入内容')
    return
  }
  submitting.value = true
  try {
    await submitComposition({ ...form })
    showToast({ message: '提交成功，等待批改 ✨', type: 'success' })
    showSubmit.value = false
    form.content = ''
    form.title = ''
    list.value = await getMyCompositions()
  } catch (e) {}
  submitting.value = false
}

onMounted(async () => {
  try { list.value = await getMyCompositions() } catch (e) {}
})
</script>

<style scoped>
.composition-page { min-height: 100vh; background: #F4F6FB; padding-bottom: 40px; }

.page-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn, .add-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.page-header h2 { flex: 1; color: #fff; font-size: 18px; font-weight: 700; margin: 0; text-align: center; }

.comp-banner {
  margin: 16px 16px 0;
  background: linear-gradient(135deg, #1A1D2E, #2D3348);
  border-radius: 16px; padding: 20px;
  display: flex; align-items: center; justify-content: space-between;
}
.cb-text h3 { margin: 0 0 4px; color: #fff; font-size: 16px; font-weight: 700; }
.cb-text p { margin: 0; color: rgba(255,255,255,0.6); font-size: 13px; }
.cb-icon { font-size: 40px; }

.type-tabs { display: flex; gap: 8px; padding: 16px 16px 8px; }
.type-btn {
  padding: 7px 18px; border-radius: 20px; border: 1.5px solid #E5E7EB;
  background: #fff; font-size: 14px; font-weight: 500; color: #6B7280;
  cursor: pointer; transition: all 0.2s;
}
.type-btn.active { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }

.comp-list { padding: 0 16px; display: flex; flex-direction: column; gap: 12px; }
.comp-item { background: #fff; border-radius: 16px; padding: 16px; box-shadow: 0 2px 8px rgba(79,110,247,0.06); }
.ci-header { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.ci-type-badge { font-size: 12px; font-weight: 700; padding: 3px 10px; border-radius: 6px; }
.ci-type-badge.writing { background: #EEF1FE; color: #4F6EF7; }
.ci-type-badge.translation { background: #F0FDF4; color: #06C073; }
.ci-score { margin-left: auto; font-size: 18px; font-weight: 800; color: #4F6EF7; }
.ci-pending { margin-left: auto; font-size: 12px; color: #9CA3AF; background: #F3F4F6; padding: 3px 8px; border-radius: 6px; }
.ci-title { font-size: 15px; font-weight: 600; color: #1A1D2E; margin-bottom: 8px; }
.ci-content {
  font-size: 14px; color: #6B7280; line-height: 1.6;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
  margin-bottom: 10px;
}
.ci-feedback { background: #EEF1FE; border-radius: 10px; padding: 10px 12px; }
.feedback-label { font-size: 12px; font-weight: 600; color: #4F6EF7; margin-bottom: 4px; }
.feedback-text { font-size: 13px; color: #374151; line-height: 1.5; }

.empty-state { padding: 60px 0; text-align: center; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-state p { color: #6B7280; margin: 0 0 4px; }
.empty-sub { color: #9CA3AF !important; font-size: 13px !important; }

.dialog-form { padding: 20px 24px; }
.df-row { margin-bottom: 16px; }
.df-row label { font-size: 13px; font-weight: 600; color: #374151; display: block; margin-bottom: 8px; }
.df-input {
  width: 100%; border: 1.5px solid #E5E7EB; border-radius: 10px;
  padding: 10px 12px; font-size: 15px; outline: none;
  color: #1A1D2E; box-sizing: border-box;
}
.df-textarea {
  width: 100%; border: 1.5px solid #E5E7EB; border-radius: 10px;
  padding: 12px; font-size: 14px; outline: none; resize: vertical;
  color: #1A1D2E; font-family: inherit; box-sizing: border-box; line-height: 1.6;
}
.df-input:focus, .df-textarea:focus { border-color: #4F6EF7; }
.word-count { text-align: right; font-size: 12px; color: #9CA3AF; margin-top: 4px; }
.type-toggle { display: flex; gap: 8px; }
.type-toggle button {
  flex: 1; padding: 8px; border-radius: 10px;
  border: 1.5px solid #E5E7EB; background: #F9FAFB;
  font-size: 14px; font-weight: 600; cursor: pointer; color: #6B7280; transition: all 0.2s;
}
.type-toggle button.active { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }
</style>