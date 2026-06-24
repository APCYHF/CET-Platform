<template>
  <div class="result-page">
    <div class="result-hero">
      <div class="result-emoji">{{ scoreEmoji }}</div>
      <div class="result-score">{{ result.correctCount }} / {{ result.totalCount }}</div>
      <div class="result-label">答题正确数</div>
      <div class="score-chip">
        预估总分 <span>{{ result.totalScore || '—' }}</span> 分
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-row">
      <div class="sr-item">
        <div class="sri-val green">{{ result.correctCount }}</div>
        <div class="sri-label">正确</div>
      </div>
      <div class="sr-divider"></div>
      <div class="sr-item">
        <div class="sri-val red">{{ result.totalCount - result.correctCount }}</div>
        <div class="sri-label">错误</div>
      </div>
      <div class="sr-divider"></div>
      <div class="sr-item">
        <div class="sri-val">{{ accuracy }}%</div>
        <div class="sri-label">正确率</div>
      </div>
    </div>

    <!-- Detail List -->
    <div class="section-label">答题详情</div>
    <div class="details-list">
      <div v-for="(d, i) in result.details" :key="d.questionId" class="detail-item">
        <div class="di-header">
          <div class="di-num" :class="d.isCorrect ? 'num-correct' : 'num-wrong'">{{ i + 1 }}</div>
          <div class="di-meta">题目 #{{ d.questionId }}</div>
          <div class="di-tag" :class="d.isCorrect ? 'tag-correct' : 'tag-wrong'">
            {{ d.isCorrect ? '✓ 正确' : '✗ 错误' }}
          </div>
        </div>
        <div class="di-answers">
          <span class="da-item your">你：{{ d.yourAnswer || '未答' }}</span>
          <span class="da-arrow">→</span>
          <span class="da-item correct">正确：{{ d.correctAnswer }}</span>
        </div>
        <div v-if="d.analysis" class="di-analysis">{{ d.analysis }}</div>
      </div>
      <div v-if="!result.details.length" class="empty-state">
        <p>暂无详情数据</p>
      </div>
    </div>

    <div class="action-row">
      <button class="btn-wrong" @click="$router.push('/wrong')">查看错题本</button>
      <button class="btn-home" @click="$router.push('/home')">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getWrongQuestions } from '@/api/question'

const result = ref({ correctCount: 0, totalCount: 0, totalScore: 0, details: [] })

const accuracy = computed(() => {
  if (!result.value.totalCount) return 0
  return Math.round((result.value.correctCount / result.value.totalCount) * 100)
})

const scoreEmoji = computed(() => {
  const a = accuracy.value
  if (a >= 90) return '🏆'
  if (a >= 70) return '🎯'
  if (a >= 50) return '💪'
  return '📚'
})

onMounted(async () => {
  try {
    const wrong = await getWrongQuestions()
    result.value.totalCount = 10
    result.value.correctCount = 10 - Math.min(wrong.length, 10)
    result.value.details = wrong.slice(0, 10).map(w => ({
      questionId: w.question?.id,
      yourAnswer: w.yourAnswer,
      correctAnswer: w.question?.answer,
      isCorrect: false,
      analysis: w.question?.analysis || ''
    }))
  } catch (e) {}
})
</script>

<style scoped>
.result-page { min-height: 100vh; background: #F4F6FB; padding-bottom: 40px; }

.result-hero {
  background: linear-gradient(145deg, #4F6EF7 0%, #6B87FF 100%);
  padding: 60px 24px 40px; text-align: center;
  border-radius: 0 0 32px 32px;
}
.result-emoji { font-size: 56px; margin-bottom: 12px; }
.result-score { font-size: 52px; font-weight: 900; color: #fff; line-height: 1; }
.result-label { color: rgba(255,255,255,0.75); font-size: 14px; margin-top: 6px; }
.score-chip {
  display: inline-block; margin-top: 16px;
  background: rgba(255,255,255,0.2); color: #fff;
  font-size: 14px; padding: 8px 20px; border-radius: 20px;
}
.score-chip span { font-weight: 800; font-size: 18px; }

.stats-row {
  margin: 20px 16px 0; background: #fff; border-radius: 16px;
  display: flex; align-items: center; justify-content: space-around;
  padding: 20px; box-shadow: 0 4px 20px rgba(79,110,247,0.1);
}
.sr-item { text-align: center; }
.sri-val { font-size: 26px; font-weight: 800; color: #1A1D2E; }
.sri-val.green { color: #06C073; }
.sri-val.red { color: #F05656; }
.sri-label { font-size: 12px; color: #9CA3AF; margin-top: 4px; }
.sr-divider { width: 1px; height: 36px; background: #E5E7EB; }

.section-label { font-size: 13px; font-weight: 600; color: #9CA3AF; padding: 20px 20px 10px; text-transform: uppercase; }
.details-list { padding: 0 16px; display: flex; flex-direction: column; gap: 8px; }

.detail-item { background: #fff; border-radius: 14px; padding: 16px; box-shadow: 0 1px 4px rgba(79,110,247,0.06); }
.di-header { display: flex; align-items: center; gap: 8px; margin-bottom: 10px; }
.di-num { width: 24px; height: 24px; border-radius: 7px; color: #fff; font-size: 12px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.num-correct { background: #06C073; }
.num-wrong { background: #F05656; }
.di-meta { flex: 1; font-size: 13px; color: #9CA3AF; }
.di-tag { font-size: 12px; font-weight: 700; padding: 3px 10px; border-radius: 6px; }
.tag-correct { background: #F0FDF4; color: #06C073; }
.tag-wrong { background: #FFF1F2; color: #F05656; }

.di-answers { display: flex; align-items: center; gap: 8px; font-size: 13px; margin-bottom: 8px; }
.da-item { padding: 4px 10px; border-radius: 6px; font-weight: 600; }
.da-item.your { background: #FFF1F2; color: #F05656; }
.da-item.correct { background: #F0FDF4; color: #06C073; }
.da-arrow { color: #9CA3AF; }
.di-analysis { font-size: 12px; color: #6B7280; background: #F4F6FB; border-radius: 8px; padding: 8px 12px; line-height: 1.6; }

.empty-state { padding: 40px; text-align: center; color: #9CA3AF; }

.action-row { display: flex; gap: 12px; padding: 24px 16px 0; }
.btn-wrong, .btn-home {
  flex: 1; height: 48px; border-radius: 14px; border: none;
  font-size: 15px; font-weight: 700; cursor: pointer;
}
.btn-wrong { background: #FFF1F2; color: #F05656; }
.btn-home { background: linear-gradient(135deg, #4F6EF7, #6B87FF); color: #fff; box-shadow: 0 4px 12px rgba(79,110,247,0.3); }
</style>