<template>
  <div class="vocab-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>生词本</h2>
      <div style="width:36px"></div>
    </div>

    <!-- Stats -->
    <div class="vocab-stats">
      <div class="vs-item">
        <div class="vs-val">{{ stats.totalWords }}</div>
        <div class="vs-label">已学</div>
      </div>
      <div class="vs-divider"></div>
      <div class="vs-item">
        <div class="vs-val" style="color:#06C073">{{ masteredCount }}</div>
        <div class="vs-label">已掌握</div>
      </div>
      <div class="vs-divider"></div>
      <div class="vs-item">
        <div class="vs-val" style="color:#FFAB30">{{ reviewCount }}</div>
        <div class="vs-label">复习中</div>
      </div>
      <div class="vs-divider"></div>
      <div class="vs-item">
        <div class="vs-val">{{ stats.masteryRate }}%</div>
        <div class="vs-label">掌握率</div>
      </div>
    </div>

    <!-- Filter -->
    <div class="filter-row">
      <button v-for="f in filters" :key="f.val"
        :class="['filter-btn', { active: activeFilter === f.val }]"
        @click="activeFilter = f.val">{{ f.label }}</button>
    </div>

    <!-- Word List -->
    <div class="word-list">
      <div v-for="item in filteredList" :key="item.vocabularyId" class="word-item">
        <div class="wi-left">
          <div class="wi-word">{{ item.word?.word }}</div>
          <div class="wi-phonetic">{{ item.word?.phonetic }}</div>
          <div class="wi-meaning">{{ item.word?.meaning }}</div>
        </div>
        <div class="wi-right">
          <div class="status-dot" :class="'dot-' + item.status"></div>
          <div class="status-text" :class="'text-' + item.status">{{ statusLabel(item.status) }}</div>
          <div v-if="item.nextReviewDate" class="review-date">{{ item.nextReviewDate }}</div>
        </div>
      </div>
      <div v-if="!filteredList.length" class="empty-state">
        <div class="empty-icon">📖</div>
        <p>{{ activeFilter === 'ALL' ? '生词本为空，去背单词添加吧' : '该分类暂无单词' }}</p>
      </div>
    </div>

    <div style="height:40px"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyVocabulary, getWordStats } from '@/api/word'

const vocabList = ref([])
const stats = ref({ totalWords: 0, masteryRate: 0 })
const activeFilter = ref('ALL')

const filters = [
  { label: '全部', val: 'ALL' },
  { label: '已掌握', val: 'MASTERED' },
  { label: '复习中', val: 'REVIEW' },
  { label: '未掌握', val: 'NEW' },
]

const filteredList = computed(() => {
  if (activeFilter.value === 'ALL') return vocabList.value
  return vocabList.value.filter(v => v.status === activeFilter.value)
})

const masteredCount = computed(() => vocabList.value.filter(v => v.status === 'MASTERED').length)
const reviewCount = computed(() => vocabList.value.filter(v => v.status === 'REVIEW').length)

function statusLabel(s) {
  return { MASTERED: '已掌握', REVIEW: '复习', NEW: '未掌握' }[s] || s
}

onMounted(async () => {
  try { vocabList.value = await getMyVocabulary() } catch (e) {}
  try { stats.value = await getWordStats() } catch (e) {}
})
</script>

<style scoped>
.vocab-page { min-height: 100vh; background: #F4F6FB; }

.page-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.page-header h2 { flex: 1; color: #fff; font-size: 18px; font-weight: 700; margin: 0; text-align: center; }

.vocab-stats {
  margin: 16px 16px 0;
  background: #fff; border-radius: 16px; padding: 20px;
  display: flex; align-items: center; justify-content: space-around;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06);
}
.vs-item { text-align: center; }
.vs-val { font-size: 22px; font-weight: 800; color: #1A1D2E; }
.vs-label { font-size: 12px; color: #9CA3AF; margin-top: 4px; }
.vs-divider { width: 1px; height: 32px; background: #E5E7EB; }

.filter-row { display: flex; gap: 8px; padding: 16px 16px 8px; }
.filter-btn {
  padding: 6px 14px; border-radius: 20px; border: 1.5px solid #E5E7EB;
  background: #fff; font-size: 13px; color: #6B7280; cursor: pointer; transition: all 0.2s;
}
.filter-btn.active { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }

.word-list { padding: 0 16px; display: flex; flex-direction: column; gap: 8px; }
.word-item {
  background: #fff; border-radius: 14px; padding: 16px;
  display: flex; align-items: flex-start; justify-content: space-between;
  box-shadow: 0 1px 4px rgba(79,110,247,0.06);
}
.wi-left { flex: 1; }
.wi-word { font-size: 18px; font-weight: 700; color: #1A1D2E; }
.wi-phonetic { font-size: 13px; color: #9CA3AF; margin-top: 2px; font-style: italic; }
.wi-meaning { font-size: 14px; color: #4F6EF7; margin-top: 6px; font-weight: 500; }

.wi-right { display: flex; flex-direction: column; align-items: flex-end; gap: 4px; margin-left: 12px; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }
.dot-MASTERED { background: #06C073; }
.dot-REVIEW { background: #FFAB30; }
.dot-NEW { background: #9CA3AF; }
.status-text { font-size: 12px; font-weight: 600; }
.text-MASTERED { color: #06C073; }
.text-REVIEW { color: #D97706; }
.text-NEW { color: #9CA3AF; }
.review-date { font-size: 11px; color: #9CA3AF; }

.empty-state { padding: 60px 0; text-align: center; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-state p { color: #9CA3AF; }
</style>