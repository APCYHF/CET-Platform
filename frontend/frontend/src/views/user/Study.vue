<template>
  <div class="study-page">
    <!-- Header -->
    <div class="study-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <div class="header-center">
        <h2>背单词</h2>
        <p>{{ currentIndex + 1 }} / {{ totalWords }}</p>
      </div>
      <div class="header-right">
        <div class="plan-badge" :class="planStatus === '已完成' ? 'done' : 'active'">
          {{ planStatus }}
        </div>
      </div>
    </div>

    <!-- Progress -->
    <div class="progress-section">
      <div class="progress-track">
        <div class="progress-fill" :style="{ width: progress + '%' }"></div>
      </div>
      <div class="progress-labels">
        <span>进度 {{ progress }}%</span>
        <span>{{ currentIndex }} / {{ totalWords }} 词</span>
      </div>
    </div>

    <!-- Word Card -->
    <div v-if="currentWord" class="card-scene">
      <div class="word-card" :class="{ flipped }" @click="flipped = !flipped">
        <div class="card-face card-front">
          <div class="word-type-badge">{{ currentWord.type || 'n.' }}</div>
          <div class="word-main">{{ currentWord.word }}</div>
          <div class="word-phonetic">{{ currentWord.phonetic || '/ˈwɜːrd/' }}</div>
          <div class="tap-hint">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M8 3L12 7L16 3" stroke="rgba(255,255,255,0.6)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 7V21" stroke="rgba(255,255,255,0.6)" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            点击翻转查看释义
          </div>
        </div>
        <div class="card-face card-back">
          <div class="back-word">{{ currentWord.word }}</div>
          <div class="back-meaning">{{ currentWord.meaning }}</div>
          <div v-if="currentWord.exampleSentence" class="back-example">
            <div class="example-label"><strong>例句</strong></div>
            <div class="example-text">{{ currentWord.exampleSentence }}</div>
          </div>
          <div v-if="currentWord.root" class="back-root">
            <span class="root-tag">词根</span> {{ currentWord.root }}
          </div>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div v-if="currentWord" class="action-area">
      <p class="action-hint">你记住这个词了吗？</p>
      <div class="action-buttons">
        <button class="action-btn btn-forget" @click="handleStatus('NEW')" :disabled="processing">
          <span class="btn-icon">😕</span>
          <span class="btn-text">不认识</span>
        </button>
        <button class="action-btn btn-fuzzy" @click="handleStatus('REVIEW')" :disabled="processing">
          <span class="btn-icon">🤔</span>
          <span class="btn-text">模糊</span>
        </button>
        <button class="action-btn btn-know" @click="handleStatus('MASTERED')" :disabled="processing">
          <span class="btn-icon">😊</span>
          <span class="btn-text">认识</span>
        </button>
      </div>
    </div>

    <!-- Completion -->
    <div v-else class="completion">
      <div class="completion-icon">🎉</div>
      <h3>今日任务完成！</h3>
      <p>最难不过坚持！</p>
      <div class="completion-stats">
        <div class="cs-item">
          <div class="cs-val">{{ totalWords }}</div>
          <div class="cs-label">学习单词</div>
        </div>
        <div class="cs-item">
          <div class="cs-val">100%</div>
          <div class="cs-label">完成率</div>
        </div>
      </div>
      <button class="btn-go-vocab" @click="$router.push('/vocabulary')">查看生词本</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTodayPlan, updateWordStatus } from '@/api/word'
import { showToast } from 'vant'

const plan = ref(null)
const wordList = ref([])
const currentIndex = ref(0)
const flipped = ref(false)
const processing = ref(false)

const currentWord = computed(() => wordList.value[currentIndex.value])
const totalWords = computed(() => wordList.value.length)
const progress = computed(() => totalWords.value ? Math.round((currentIndex.value / totalWords.value) * 100) : 0)
const planStatus = computed(() => plan.value?.completed ? '已完成' : '进行中')

async function handleStatus(status) {
  if (!currentWord.value || processing.value) return
  processing.value = true
  try {
    await updateWordStatus(currentWord.value.id, status)
    if (currentIndex.value < totalWords.value - 1) {
      flipped.value = false
      setTimeout(() => { currentIndex.value++; processing.value = false }, 150)
    } else {
      showToast({ message: '🎉 今日单词已完成！', type: 'success' })
      wordList.value = []
      processing.value = false
    }
  } catch (e) { processing.value = false }
}

onMounted(async () => {
  try {
    const data = await getTodayPlan()
    plan.value = data.plan
    wordList.value = [...(data.reviewWords || []).map(w => w.word), ...(data.newWords || [])].filter(Boolean)
  } catch (e) {}
})
</script>

<style scoped>
.study-page { min-height: 100vh; background: #F4F6FB; }

.study-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2);
  color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.header-center { flex: 1; text-align: center; }
.header-center h2 { margin: 0; color: #fff; font-size: 18px; font-weight: 700; }
.header-center p { margin: 2px 0 0; color: rgba(255,255,255,0.7); font-size: 13px; }
.header-right { width: 36px; display: flex; justify-content: flex-end; }
.plan-badge {
  font-size: 11px; font-weight: 600; padding: 4px 10px; border-radius: 20px;
  white-space: nowrap;
}
.plan-badge.active { background: rgba(255,255,255,0.25); color: #fff; }
.plan-badge.done { background: #06C073; color: #fff; }

.progress-section { padding: 16px 20px 8px; }
.progress-track { height: 6px; background: #E5E7EB; border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #4F6EF7, #6B87FF); border-radius: 3px; transition: width 0.4s; }
.progress-labels { display: flex; justify-content: space-between; font-size: 12px; color: #9CA3AF; margin-top: 6px; }

/* Card */
.card-scene { padding: 16px 20px; perspective: 1200px; }
.word-card {
  height: 300px; cursor: pointer;
  transform-style: preserve-3d;
  transition: transform 0.55s cubic-bezier(0.4, 0.2, 0.2, 1);
  position: relative;
}
.word-card.flipped { transform: rotateY(180deg); }
.card-face {
  position: absolute; inset: 0;
  border-radius: 24px;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 28px 24px;
  box-shadow: 0 8px 32px rgba(79,110,247,0.15);
}
.card-front { background: linear-gradient(145deg, #2D88FF 0%, #0066FF 100%); }
.card-back { background: #fff; transform: rotateY(180deg); }

.word-type-badge {
  background: rgba(255,255,255,0.2); color: #fff;
  font-size: 12px; font-weight: 600;
  padding: 3px 12px; border-radius: 20px;
  margin-bottom: 16px;
}
.word-main { font-size: 38px; font-weight: 800; color: #fff; letter-spacing: -1px; }
.word-phonetic { color: rgba(255,255,255,0.75); font-size: 15px; margin-top: 8px; font-style: italic; }
.tap-hint {
  position: absolute; bottom: 20px;
  color: rgba(255,255,255,0.5); font-size: 12px;
  display: flex; align-items: center; gap: 4px;
}

.back-word { font-size: 24px; font-weight: 700; color: #1A1D2E; margin-bottom: 12px; }
.back-meaning { font-size: 18px; color: #4F6EF7; font-weight: 600; text-align: center; margin-bottom: 16px; }
.back-example { background: #F4F6FB; border-radius: 10px; padding: 10px 14px; width: 100%; }
.example-label strong{ font-size: 11px; color: #9CA3AF; font-weight: 600; margin-bottom: 4px; }
.example-text { font-size: 17px !important; color: #333; line-height: 1.6; margin-top: 6px;}
.back-root { font-size: 13px; color: #9CA3AF; margin-top: 10px; display: flex; align-items: center; gap: 6px; }
.root-tag { background: #EEF1FE; color: #4F6EF7; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 4px; }

/* Actions */
.action-area { padding: 8px 20px 0; }
.action-hint { text-align: center; color: #9CA3AF; font-size: 14px; margin: 0 0 16px; }
.action-buttons { display: flex; gap: 12px; }
.action-btn {
  flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px;
  padding: 14px 8px; border-radius: 16px; border: none; cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.action-btn:active { transform: scale(0.96); }
.action-btn:disabled { opacity: 0.5; }
.btn-icon { font-size: 24px; }
.btn-text { font-size: 13px; font-weight: 600; }

.btn-forget { background: #FFF1F2; color: #F05656; }
.btn-fuzzy { background: #FFFBEB; color: #D97706; }
.btn-know { background: #E8F8E8; color: #009944; }

/* Completion */
.completion {
  display: flex; flex-direction: column; align-items: center;
  padding: 60px 24px;
  text-align: center;
}
.completion-icon { font-size: 72px; margin-bottom: 20px; }
.completion h3 { font-size: 24px; font-weight: 700; color: #1A1D2E; margin: 0 0 8px; }
.completion p { color: #9CA3AF; margin: 0 0 28px; }
.completion-stats { display: flex; gap: 40px; margin-bottom: 32px; }
.cs-item { text-align: center; }
.cs-val { font-size: 28px; font-weight: 800; color: #4F6EF7; }
.cs-label { font-size: 13px; color: #9CA3AF; margin-top: 4px; }
.btn-go-vocab {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  color: #fff; border: none; border-radius: 50px;
  padding: 14px 40px; font-size: 16px; font-weight: 700; cursor: pointer;
  box-shadow: 0 4px 16px rgba(79,110,247,0.35);
}
</style>