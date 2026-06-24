<template>
  <div>
    <h2>批改管理</h2>
    <el-table :data="corrections" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="type" label="类型" width="80" />
      <el-table-column prop="title" label="题目" min-width="200" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="aiScore" label="AI评分" width="80" />
      <el-table-column prop="manualStatus" label="批改状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.manualStatus)">{{ statusLabel(row.manualStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.manualStatus === 'PENDING'" type="primary" size="small" @click="assign(row)">分配批改</el-button>
          <el-tag v-else>{{ row.teacherId ? '已分配' : '' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPendingCorrections, assignCorrection } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const corrections = ref([])
const loading = ref(false)

function statusType(s) {
  return { NONE: 'info', PENDING: 'warning', ASSIGNED: 'primary', COMPLETED: 'success' }[s] || 'default'
}
function statusLabel(s) {
  return { NONE: '无需批改', PENDING: '待分配', ASSIGNED: '已分配', COMPLETED: '已完成' }[s] || s
}

async function loadCorrections() {
  loading.value = true
  try {
    corrections.value = await getPendingCorrections()
  } catch (e) {}
  loading.value = false
}

async function assign(row) {
  ElMessageBox.prompt('输入教师用户ID', '分配批改任务', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(async ({ value }) => {
    if (value) {
      await assignCorrection(row.id, Number(value))
      ElMessage.success('分配成功')
      loadCorrections()
    }
  }).catch(() => {})
}

onMounted(loadCorrections)
</script>
