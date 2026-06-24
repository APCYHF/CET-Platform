<template>
  <div class="reading-detail">
    <div class="exam-header">
      <button class="back-btn" @click="confirmExit">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <div class="timer-display">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="9" stroke="white" stroke-width="1.8"/>
          <path d="M12 7V12L15 15" stroke="white" stroke-width="1.8" stroke-linecap="round"/>
        </svg>
        {{ timerDisplay }}
      </div>
    </div>

    <div v-if="question" class="content-area">
      <div class="passage-card">
        <div class="passage-label">阅读原文</div>
        <div class="passage-text">{{ question.content }}</div>
      </div>

      <div class="options-card">
        <div class="options-label">请选择正确答案</div>
        <div class="options-list">
          <label
            v-for="(opt, i) in question.options" :key="i"
            class="option-item"
            :class="{
              selected: selected === String.fromCharCode(65+i),
              correct: showResult && String.fromCharCode(65+i) === question.answer,
              wrong: showResult && selected === String.fromCharCode(65+i) && !isCorrect
            }"
          >
            <input type="radio" :value="String.fromCharCode(65+i)" v-model="selected" style="display:none" :disabled="showResult" />
            <span class="opt-letter">{{ String.fromCharCode(65+i) }}</span>
            <span class="opt-text">{{ opt }}</span>
          </label>
        </div>
      </div>

      <!-- Result Panel -->
      <div v-if="showResult" class="result-panel" :class="isCorrect ? 'panel-correct' : 'panel-wrong'">
        <div class="rp-header">
          <span class="rp-icon">{{ isCorrect ? '✓' : '✗' }}</span>
          <span class="rp-title">{{ isCorrect ? '回答正确！' : '回答错误' }}</span>
          <span class="rp-answer">正确答案：{{ question.answer }}</span>
        </div>
        <div v-if="question.analysis" class="rp-analysis">{{ question.analysis }}</div>
      </div>
    </div>

    <!-- Bottom Action -->
    <div class="bottom-bar">
      <button v-if="!showResult" class="btn-submit" @click="handleSubmit" :disabled="!selected || submitting">
        提交答案
      </button>
      <button v-else class="btn-next" @click="$router.back()">
        返回练习
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showDialog } from 'vant'
import { getReadingQuestions, submitAnswers } from '@/api/question'

const route = useRoute()
const router = useRouter()
const question = ref(null)
const selected = ref('')
const showResult = ref(false)
const submitting = ref(false)
const isCorrect = ref(false)
const seconds = ref(0)
let timer = null

const timerDisplay = computed(() => {
  const m = Math.floor(seconds.value / 60)
  const s = seconds.value % 60
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
})

async function handleSubmit() {
  if (!selected.value) return
  submitting.value = true
  try {
    const res = await submitAnswers({
      paperId: null,
      answers: [{ questionId: question.value.id, answer: selected.value, spendTime: seconds.value }]
    })
    isCorrect.value = res.details?.[0]?.isCorrect ?? false
    showResult.value = true
  } catch (e) {}
  submitting.value = false
}

function confirmExit() {
  if (showResult.value) { router.back(); return }
  showDialog({ title: '退出练习', message: '确定退出？', confirmButtonColor: '#F05656' })
    .then(() => router.back()).catch(() => {})
}

onMounted(async () => {
  timer = setInterval(() => seconds.value++, 1000)
  try {
    const list = await getReadingQuestions()
    question.value = list.find(q => q.id == route.params.id) || list[0]
  } catch (e) {}
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.reading-detail { min-height: 100vh; background: #F4F6FB; padding-bottom: 80px; }

.exam-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 16px;
  display: flex; align-items: center; justify-content: space-between;
}
.back-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.timer-display {
  display: flex; align-items: center; gap: 6px;
  color: #fff; font-size: 15px; font-weight: 700;
  background: rgba(255,255,255,0.15); padding: 6px 14px; border-radius: 20px;
}

.content-area { padding: 16px; }
.passage-card { background: #fff; border-radius: 16px; padding: 20px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(79,110,247,0.06); }
.passage-label { font-size: 12px; font-weight: 700; color: #9CA3AF; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 12px; }
.passage-text { font-size: 15px; color: #374151; line-height: 1.8; }

.options-card { background: #fff; border-radius: 16px; padding: 20px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(79,110,247,0.06); }
.options-label { font-size: 13px; font-weight: 700; color: #1A1D2E; margin-bottom: 14px; }
.options-list { display: flex; flex-direction: column; gap: 10px; }

.option-item {
  display: flex; align-items: center; gap: 12px; padding: 14px 14px;
  border-radius: 12px; border: 1.5px solid #E5E7EB; cursor: pointer;
  transition: all 0.2s;
}
.option-item.selected { border-color: #4F6EF7; background: #EEF1FE; }
.option-item.correct { border-color: #06C073; background: #F0FDF4; }
.option-item.wrong { border-color: #F05656; background: #FFF1F2; }

.opt-letter {
  width: 30px; height: 30px; border-radius: 50%;
  border: 1.5px solid currentColor; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; flex-shrink: 0; color: #9CA3AF;
}
.option-item.selected .opt-letter { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }
.option-item.correct .opt-letter { background: #06C073; border-color: #06C073; color: #fff; }
.option-item.wrong .opt-letter { background: #F05656; border-color: #F05656; color: #fff; }
.opt-text { font-size: 14px; color: #374151; }

.result-panel {
  border-radius: 16px; padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.panel-correct { background: linear-gradient(135deg, #F0FDF4, #DCFCE7); border: 1px solid #BBF7D0; }
.panel-wrong { background: linear-gradient(135deg, #FFF1F2, #FFE4E6); border: 1px solid #FEE2E2; }

.rp-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.rp-icon { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 16px; color: #fff; }
.panel-correct .rp-icon { background: #06C073; }
.panel-wrong .rp-icon { background: #F05656; }
.rp-title { font-size: 15px; font-weight: 700; flex: 1; }
.panel-correct .rp-title { color: #065F46; }
.panel-wrong .rp-title { color: #991B1B; }
.rp-answer { font-size: 13px; color: #6B7280; font-weight: 600; }
.rp-analysis { font-size: 13px; color: #374151; line-height: 1.6; }

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 12px 16px 28px; background: #fff;
  border-top: 1px solid #E5E7EB;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.06);
}
.btn-submit, .btn-next {
  width: 100%; height: 50px; border-radius: 14px; border: none;
  font-size: 16px; font-weight: 700; cursor: pointer;
}
.btn-submit {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF); color: #fff;
  box-shadow: 0 4px 12px rgba(79,110,247,0.3);
}
.btn-submit:disabled { opacity: 0.5; }
.btn-next { background: #F4F6FB; color: #374151; border: 1.5px solid #E5E7EB; }
</style>