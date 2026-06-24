<template>
  <div class="listening-page">
    <van-nav-bar title="听力练习" left-arrow @click-left="$router.back()" />
    <van-cell-group inset style="margin:12px">
      <van-cell v-for="q in questions" :key="q.id" :title="'听力题 #' + q.id" :label="q.subType" is-link @click="$router.push('/practice/listening/' + q.id)" />
      <van-empty v-if="!questions.length" description="暂无听力题" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getListeningQuestions } from '@/api/question'

const questions = ref([])

onMounted(async () => {
  try { questions.value = await getListeningQuestions() } catch (e) {}
})
</script>
