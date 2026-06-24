<template>
  <div class="wrong-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>错题本</h2>
      <div style="width:36px"></div>
    </div>

    <!-- Summary -->
    <div class="summary-card">
      <div class="summary-circle">
        <div class="sc-num">{{ wrongList.length }}</div>
        <div class="sc-label">道错题</div>
      </div>
      <div class="summary-text">
        <p>查漏补缺，逐一击破</p>
        <p class="sub">每次复习都是提升的机会</p>
      </div>
    </div>

    <div v-if="wrongList.length" class="wrong-list">
      <div v-for="(w, i) in wrongList" :key="w.recordId" class="wrong-item">
        <div class="wi-header">
          <span class="wi-num">{{ i + 1 }}</span>
          <span class="wi-type">{{ w.question?.type }} · {{ w.question?.subType }}</span>
          <span class="wi-error-tag">错误</span>
        </div>
        <div class="wi-content">{{ w.question?.content }}</div>
        <div class="wi-answers">
          <div class="answer-row your-ans">
            <span class="ans-label">你的答案</span>
            <span class="ans-val wrong">{{ w.yourAnswer || '未作答' }}</span>
          </div>
          <div class="answer-row correct-ans">
            <span class="ans-label">正确答案</span>
            <span class="ans-val correct">{{ w.question?.answer }}</span>
          </div>
        </div>
        <div v-if="w.question?.analysis" class="wi-analysis">
          <div class="analysis-label">📝 解析</div>
          <div class="analysis-text">{{ w.question.analysis }}</div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">🎯</div>
      <h3>错题本是空的</h3>
      <p>太棒了，继续保持！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getWrongQuestions } from '@/api/question'

const wrongList = ref([])

onMounted(async () => {
  try { wrongList.value = await getWrongQuestions() } catch (e) {}
})
</script>

<style scoped>
.wrong-page { min-height: 100vh; background: #F4F6FB; padding-bottom: 40px; }

.page-header {
  background: linear-gradient(135deg, #F05656, #FB7185);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.page-header h2 { flex: 1; color: #fff; font-size: 18px; font-weight: 700; margin: 0; text-align: center; }

.summary-card {
  margin: 20px 16px 0;
  background: linear-gradient(135deg, #FFF1F2, #FFE4E6);
  border-radius: 16px; padding: 20px;
  display: flex; align-items: center; gap: 20px;
  border: 1px solid #FEE2E2;
}
.summary-circle {
  width: 72px; height: 72px; border-radius: 50%;
  background: #F05656; display: flex; flex-direction: column;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.sc-num { font-size: 24px; font-weight: 800; color: #fff; line-height: 1; }
.sc-label { font-size: 11px; color: rgba(255,255,255,0.85); }
.summary-text p { margin: 0 0 4px; font-size: 15px; font-weight: 600; color: #991B1B; }
.summary-text .sub { font-size: 13px; color: #9CA3AF; font-weight: 400; }

.wrong-list { padding: 16px 16px 0; display: flex; flex-direction: column; gap: 12px; }
.wrong-item { background: #fff; border-radius: 16px; padding: 16px; box-shadow: 0 2px 8px rgba(240,86,86,0.08); }

.wi-header { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.wi-num {
  width: 22px; height: 22px; border-radius: 6px;
  background: #F05656; color: #fff; font-size: 12px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.wi-type { flex: 1; font-size: 13px; color: #9CA3AF; }
.wi-error-tag { background: #FEE2E2; color: #F05656; font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 4px; }

.wi-content {
  font-size: 15px; color: #1A1D2E; line-height: 1.6; margin-bottom: 14px;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}

.wi-answers { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
.answer-row { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 8px; }
.your-ans { background: #FFF1F2; }
.correct-ans { background: #F0FDF4; }
.ans-label { font-size: 12px; color: #9CA3AF; width: 60px; flex-shrink: 0; }
.ans-val { font-size: 14px; font-weight: 700; }
.ans-val.wrong { color: #F05656; }
.ans-val.correct { color: #06C073; }

.wi-analysis { background: #F4F6FB; border-radius: 10px; padding: 12px; }
.analysis-label { font-size: 12px; font-weight: 600; color: #6B7280; margin-bottom: 6px; }
.analysis-text { font-size: 13px; color: #374151; line-height: 1.6; }

.empty-state { padding: 80px 0; text-align: center; }
.empty-icon { font-size: 72px; margin-bottom: 16px; }
.empty-state h3 { font-size: 20px; font-weight: 700; color: #1A1D2E; margin: 0 0 8px; }
.empty-state p { color: #9CA3AF; }
</style>