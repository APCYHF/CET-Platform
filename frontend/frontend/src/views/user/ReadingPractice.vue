<template>
  <div class="reading-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>阅读刷题</h2>
      <div style="width:36px"></div>
    </div>

    <!-- Exam Papers -->
    <div v-if="papers.length" class="section">
      <div class="section-title">🏆 整套真题</div>
      <div class="papers-list">
        <div v-for="p in papers" :key="p.id" class="paper-card" @click="$router.push('/paper/' + p.id)">
          <div class="pc-left">
            <div class="pc-badge" :class="p.level === 'CET6' ? 'badge-six' : 'badge-four'">{{ p.level || 'CET4' }}</div>
            <div class="pc-info">
              <div class="pc-title">{{ p.title }}</div>
              <div class="pc-meta">{{ p.year }} 年 · {{ p.listenTime + p.readTime + p.writeTime + p.transTime || 180 }} 分钟</div>
            </div>
          </div>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M9 18L15 12L9 6" stroke="#9CA3AF" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- Single Questions -->
    <div class="section">
      <div class="section-title">📖 单题练习</div>
      <div v-if="questions.length" class="questions-list">
        <div v-for="q in questions" :key="q.id" class="question-card" @click="$router.push('/practice/reading/' + q.id)">
          <div class="qc-badge">阅读</div>
          <div class="qc-content">{{ q.content }}</div>
          <div class="qc-meta">{{ q.subType }}</div>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
            <path d="M9 18L15 12L9 6" stroke="#9CA3AF" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">📚</div>
        <p>暂无练习题</p>
      </div>
    </div>
    <div style="height:80px"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getReadingQuestions, getPapers } from '@/api/question'

const papers = ref([])
const questions = ref([])

onMounted(async () => {
  try { papers.value = await getPapers('CET4') } catch (e) {}
  try { questions.value = await getReadingQuestions() } catch (e) {}
})
</script>

<style scoped>
.reading-page { min-height: 100vh; background: #F4F6FB; }

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

.section { padding: 20px 16px 0; }
.section-title { font-size: 15px; font-weight: 700; color: #1A1D2E; margin-bottom: 12px; }

.papers-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 0; }
.paper-card {
  background: #fff; border-radius: 14px; padding: 16px;
  display: flex; align-items: center; justify-content: space-between;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06); cursor: pointer;
  transition: transform 0.15s;
}
.paper-card:active { transform: scale(0.98); }
.pc-left { display: flex; align-items: center; gap: 12px; }
.pc-badge { font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 8px; flex-shrink: 0; }
.badge-four { background: #EEF1FE; color: #4F6EF7; }
.badge-six { background: #F0FDF4; color: #06C073; }
.pc-title { font-size: 14px; font-weight: 600; color: #1A1D2E; margin-bottom: 2px; }
.pc-meta { font-size: 12px; color: #9CA3AF; }

.questions-list { display: flex; flex-direction: column; gap: 8px; }
.question-card {
  background: #fff; border-radius: 14px; padding: 16px;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06); cursor: pointer;
  transition: transform 0.15s;
  display: flex; align-items: flex-start; gap: 10px;
}
.question-card:active { transform: scale(0.98); }
.qc-badge { background: #EEF1FE; color: #4F6EF7; font-size: 11px; font-weight: 700; padding: 3px 8px; border-radius: 6px; flex-shrink: 0; margin-top: 1px; }
.qc-content { flex: 1; font-size: 14px; color: #1A1D2E; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.qc-meta { font-size: 12px; color: #9CA3AF; }

.empty-state { padding: 40px 0; text-align: center; }
.empty-icon { font-size: 40px; margin-bottom: 8px; }
.empty-state p { color: #9CA3AF; }
</style>