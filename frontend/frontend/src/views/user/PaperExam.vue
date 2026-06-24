<template>
  <div class="paper-exam">
    <!-- Header with timer -->
    <div class="exam-header">
      <button class="exit-btn" @click="confirmExit">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <div class="header-center">
        <div class="exam-title">{{ paper?.title || '真题模拟' }}</div>
        <div class="exam-progress">{{ answeredCount }} / {{ totalCount }} 题已作答</div>
      </div>
      <div class="timer-box" :class="{ warning: seconds > 7200 }">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
          <path d="M12 7V12L15 15" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
        </svg>
        <span>{{ timerDisplay }}</span>
      </div>
    </div>

    <!-- Tab navigation -->
    <div class="section-tabs">
      <button
        v-for="(tab, i) in sectionTabs" :key="i"
        :class="['st-btn', { active: activeSection === i }]"
        @click="activeSection = i"
      >
        <span>{{ tab.label }}</span>
        <span class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Questions -->
    <div class="questions-area" v-if="paper">
      <!-- Listening -->
      <div v-show="activeSection === 0">
        <div v-if="listening.length">
          <div v-for="(q, qi) in listening" :key="q.id" class="question-block">
            <div class="qb-header">
              <span class="qb-num">{{ qi + 1 }}</span>
              <span class="qb-text">{{ q.content }}</span>
            </div>
            <div class="options-list">
              <label v-for="(opt, i) in q.options" :key="i" class="option-item" :class="{ selected: answers[q.id] === String.fromCharCode(65+i) }">
                <input type="radio" :name="'q' + q.id" :value="String.fromCharCode(65+i)" v-model="answers[q.id]" style="display:none" />
                <span class="opt-letter">{{ String.fromCharCode(65+i) }}</span>
                <span class="opt-text">{{ opt }}</span>
              </label>
            </div>
          </div>
        </div>
        <div v-else class="empty-section">暂无听力题目</div>
      </div>

      <!-- Reading -->
      <div v-show="activeSection === 1">
        <div v-if="reading.length">
          <div v-for="(q, qi) in reading" :key="q.id" class="question-block">
            <div class="qb-header">
              <span class="qb-num">{{ qi + 1 }}</span>
              <span class="qb-text">{{ q.content }}</span>
            </div>
            <div class="options-list">
              <label v-for="(opt, i) in q.options" :key="i" class="option-item" :class="{ selected: answers[q.id] === String.fromCharCode(65+i) }">
                <input type="radio" :name="'q' + q.id" :value="String.fromCharCode(65+i)" v-model="answers[q.id]" style="display:none" />
                <span class="opt-letter">{{ String.fromCharCode(65+i) }}</span>
                <span class="opt-text">{{ opt }}</span>
              </label>
            </div>
          </div>
        </div>
        <div v-else class="empty-section">暂无阅读题目</div>
      </div>

      <!-- Writing -->
      <div v-show="activeSection === 2">
        <div v-if="writing.length">
          <div v-for="q in writing" :key="q.id" class="question-block">
            <div class="qb-text" style="margin-bottom:12px">{{ q.content }}</div>
            <textarea v-model="answers[q.id]" class="essay-input" placeholder="在此输入作文内容..." rows="10"></textarea>
            <div class="word-count">字数：{{ (answers[q.id] || '').length }}</div>
          </div>
        </div>
        <div v-else class="empty-section">暂无写作题目</div>
      </div>

      <!-- Translation -->
      <div v-show="activeSection === 3">
        <div v-if="translation.length">
          <div v-for="q in translation" :key="q.id" class="question-block">
            <div class="qb-text" style="margin-bottom:12px">{{ q.content }}</div>
            <textarea v-model="answers[q.id]" class="essay-input" placeholder="在此输入翻译内容..." rows="8"></textarea>
          </div>
        </div>
        <div v-else class="empty-section">暂无翻译题目</div>
      </div>
    </div>

    <!-- Submit -->
    <div class="submit-area">
      <div class="submit-info">已答 {{ answeredCount }} / {{ totalCount }} 题</div>
      <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交全部答案' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPaperDetail, submitAnswers } from '@/api/question'
import { showDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const paper = ref(null)
const listening = ref([])
const reading = ref([])
const writing = ref([])
const translation = ref([])
const answers = reactive({})
const activeSection = ref(0)
const submitting = ref(false)
const seconds = ref(0)
let timer = null

const allQuestions = computed(() => [...listening.value, ...reading.value, ...writing.value, ...translation.value])
const totalCount = computed(() => allQuestions.value.length)
const answeredCount = computed(() => Object.keys(answers).filter(k => answers[k]).length)

const sectionTabs = computed(() => [
  { label: '听力', count: listening.value.length },
  { label: '阅读', count: reading.value.length },
  { label: '写作', count: writing.value.length },
  { label: '翻译', count: translation.value.length },
])

const timerDisplay = computed(() => {
  const m = Math.floor(seconds.value / 60)
  const s = seconds.value % 60
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
})

function confirmExit() {
  showDialog({ title: '退出考试', message: '确定退出？作答记录不会保存。', confirmButtonColor: '#F05656' })
    .then(() => router.back()).catch(() => {})
}

async function handleSubmit() {
  const unanswered = totalCount.value - answeredCount.value
  if (unanswered > 0) {
    try {
      await showDialog({ title: '还有未答题目', message: `还有 ${unanswered} 题未作答，确定提交？`, confirmButtonText: '仍然提交' })
    } catch { return }
  }
  submitting.value = true
  try {
    const ansList = allQuestions.value.map(q => ({
      questionId: q.id,
      answer: answers[q.id] || '',
      spendTime: Math.floor(seconds.value / allQuestions.value.length)
    }))
    await submitAnswers({ paperId: paper.value?.id, answers: ansList })
    showToast({ message: '提交成功', type: 'success' })
    router.push('/paper/' + route.params.id + '/result')
  } catch (e) {}
  submitting.value = false
}

onMounted(async () => {
  timer = setInterval(() => seconds.value++, 1000)
  try {
    const data = await getPaperDetail(route.params.id)
    paper.value = data.paper
    listening.value = data.listening || []
    reading.value = data.reading || []
    writing.value = data.writing || []
    translation.value = data.translation || []
  } catch (e) {}
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.paper-exam { min-height: 100vh; background: #F4F6FB; padding-bottom: 80px; }

.exam-header {
  background: linear-gradient(135deg, #1A1D2E, #2D3348);
  padding: 52px 16px 16px;
  display: flex; align-items: center; gap: 12px;
  position: sticky; top: 0; z-index: 10;
}
.exit-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.1); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.header-center { flex: 1; text-align: center; }
.exam-title { color: #fff; font-size: 15px; font-weight: 700; }
.exam-progress { color: rgba(255,255,255,0.6); font-size: 12px; margin-top: 2px; }
.timer-box {
  display: flex; align-items: center; gap: 4px;
  background: rgba(255,255,255,0.15); color: #fff;
  padding: 6px 12px; border-radius: 20px; font-size: 14px; font-weight: 700;
  transition: background 0.3s;
}
.timer-box.warning { background: rgba(240,86,86,0.8); }

.section-tabs {
  display: flex; background: #fff;
  border-bottom: 1px solid #E5E7EB;
  position: sticky; top: 72px; z-index: 9;
}
.st-btn {
  flex: 1; padding: 12px 4px; border: none; background: none;
  display: flex; flex-direction: column; align-items: center; gap: 2px;
  cursor: pointer; color: #9CA3AF; font-size: 13px; font-weight: 500;
  transition: color 0.2s; border-bottom: 2px solid transparent;
}
.st-btn.active { color: #4F6EF7; border-bottom-color: #4F6EF7; }
.tab-count { font-size: 11px; background: #F3F4F6; border-radius: 20px; padding: 1px 7px; }
.st-btn.active .tab-count { background: #EEF1FE; color: #4F6EF7; }

.questions-area { padding: 16px; }
.question-block { background: #fff; border-radius: 16px; padding: 20px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(79,110,247,0.06); }
.qb-header { display: flex; gap: 10px; margin-bottom: 16px; }
.qb-num {
  width: 24px; height: 24px; border-radius: 7px; background: #4F6EF7;
  color: #fff; font-size: 12px; font-weight: 700; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.qb-text { font-size: 15px; color: #1A1D2E; line-height: 1.6; flex: 1; }

.options-list { display: flex; flex-direction: column; gap: 8px; }
.option-item {
  display: flex; align-items: center; gap: 10px; padding: 12px 14px;
  border-radius: 12px; border: 1.5px solid #E5E7EB; cursor: pointer;
  transition: all 0.15s;
}
.option-item.selected { border-color: #4F6EF7; background: #EEF1FE; }
.opt-letter {
  width: 28px; height: 28px; border-radius: 50%;
  border: 1.5px solid currentColor; display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; flex-shrink: 0;
  color: #9CA3AF; transition: all 0.15s;
}
.option-item.selected .opt-letter { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }
.opt-text { font-size: 14px; color: #374151; flex: 1; }
.option-item.selected .opt-text { color: #1A1D2E; }

.essay-input {
  width: 100%; border: 1.5px solid #E5E7EB; border-radius: 12px;
  padding: 14px; font-size: 15px; resize: vertical; outline: none;
  color: #1A1D2E; line-height: 1.6; font-family: inherit; box-sizing: border-box;
}
.essay-input:focus { border-color: #4F6EF7; }
.word-count { text-align: right; font-size: 12px; color: #9CA3AF; margin-top: 6px; }

.empty-section { padding: 40px; text-align: center; color: #9CA3AF; }

.submit-area {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 12px 16px 28px;
  border-top: 1px solid #E5E7EB;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.06);
  display: flex; align-items: center; gap: 16px;
}
.submit-info { font-size: 13px; color: #9CA3AF; white-space: nowrap; }
.btn-submit {
  flex: 1; height: 48px;
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  color: #fff; border: none; border-radius: 14px;
  font-size: 16px; font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 12px rgba(79,110,247,0.3);
}
.btn-submit:disabled { opacity: 0.7; }
</style>