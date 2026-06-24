<template>
  <div class="listening-detail">
    <van-nav-bar :title="'听力练习 - ' + timerDisplay" left-arrow @click-left="confirmExit" />

    <div v-if="question" class="content">
      <div class="audio-box">
        <van-icon name="music" size="40" color="#1989fa" />
        <p>{{ playing ? '播放中...' : '点击播放' }}</p>
        <van-button :icon="playing ? 'pause-circle' : 'play-circle'" @click="togglePlay" round plain type="primary">{{ playing ? '暂停' : '播放' }}</van-button>
        <div style="margin-top:8px;display:flex;gap:8px;justify-content:center">
          <van-tag v-for="rate in rates" :key="rate" :type="speed === rate ? 'primary' : 'default'" @click="speed = rate">{{ rate }}x</van-tag>
        </div>
      </div>

      <div v-if="showText" class="transcript" v-html="question.listeningText"></div>

      <div style="padding:0 16px">
        <p style="font-weight:bold">{{ question.content }}</p>
        <van-radio-group v-model="selected" direction="vertical">
          <van-radio v-for="(opt, i) in question.options" :key="i" :name="String.fromCharCode(65 + i)" style="margin-bottom:12px">{{ opt }}</van-radio>
        </van-radio-group>
      </div>
    </div>

    <div style="padding:16px;display:flex;gap:12px">
      <van-button round block type="primary" @click="handleSubmit" :loading="submitting">提交答案</van-button>
      <van-button round block @click="showText = !showText">{{ showText ? '隐藏' : '显示' }}原文</van-button>
    </div>

    <van-dialog v-model:show="showResult" title="答题结果" @confirm="$router.back()">
      <div style="padding:20px;text-align:center">
        <p>你的答案: {{ selected || '未答' }}</p>
        <p>正确答案: {{ question.answer }}</p>
        <p :style="{ color: isCorrect ? '#07c160' : '#ee0a24' }">{{ isCorrect ? '✓ 正确' : '✗ 错误' }}</p>
        <div style="text-align:left;background:#f7f8fa;padding:12px;border-radius:8px">{{ question.analysis }}</div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getListeningQuestions, submitAnswers } from '@/api/question'
import { showDialog } from 'vant'

const route = useRoute()
const router = useRouter()
const question = ref(null)
const selected = ref('')
const showResult = ref(false)
const submitting = ref(false)
const isCorrect = ref(false)
const playing = ref(false)
const showText = ref(false)
const speed = ref(1)
const rates = [0.8, 1, 1.2, 1.5]
const seconds = ref(0)
let timer = null

const timerDisplay = computed(() => {
  const m = Math.floor(seconds.value / 60)
  const s = seconds.value % 60
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
})

function togglePlay() { playing.value = !playing.value }
function confirmExit() { showDialog({ title: '提示', message: '确定退出？' }).then(() => router.back()) }

async function handleSubmit() {
  if (!selected.value) return
  submitting.value = true
  try {
    const res = await submitAnswers({
      paperId: null,
      answers: [{ questionId: question.value.id, answer: selected.value, spendTime: seconds.value }]
    })
    isCorrect.value = res.details[0]?.isCorrect
    showResult.value = true
  } catch (e) {}
  submitting.value = false
}

onMounted(async () => {
  timer = setInterval(() => seconds.value++, 1000)
  try {
    const list = await getListeningQuestions()
    question.value = list.find(q => q.id == route.params.id) || list[0]
  } catch (e) {}
})

onUnmounted(() => { clearInterval(timer) })
</script>

<style scoped>
.content { padding: 16px; }
.audio-box { background: #fff; border-radius: 8px; padding: 24px; text-align: center; margin-bottom: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
.transcript { background: #f7f8fa; border-radius: 8px; padding: 12px; margin: 0 0 16px; font-size: 13px; line-height: 1.8; }
</style>
