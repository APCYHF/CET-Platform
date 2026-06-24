<template>
  <div class="dashboard">
    <h2>数据看板</h2>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in cards" :key="item.label">
        <el-card shadow="hover">
          <div class="card-item">
            <p class="card-label">{{ item.label }}</p>
            <p class="card-value">{{ item.value }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin'

const cards = ref([
  { label: '用户总数', value: 0 },
  { label: '题目总数', value: 0 },
  { label: '订单总数', value: 0 },
  { label: '今日答题', value: 0 }
])

onMounted(async () => {
  try {
    const data = await getDashboard()
    cards.value = [
      { label: '用户总数', value: data.totalUsers },
      { label: '题目总数', value: data.totalQuestions },
      { label: '订单总数', value: data.totalOrders },
      { label: '今日答题', value: data.todayAnswers }
    ]
  } catch (e) {}
})
</script>

<style scoped>
.dashboard { padding: 20px; }
.card-item { text-align: center; padding: 10px 0; }
.card-label { color: #999; font-size: 14px; }
.card-value { color: #333; font-size: 28px; font-weight: bold; margin-top: 8px; }
</style>
