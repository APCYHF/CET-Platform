<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">试卷管理</h2>
      <el-button type="primary" @click="showForm = true">创建试卷</el-button>
    </div>

    <el-table :data="papers" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="level" label="等级" width="80" />
      <el-table-column prop="paperType" label="类型" width="80" />
      <el-table-column prop="year" label="年份" width="60" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showForm" title="创建试卷" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="等级">
          <el-select v-model="form.level">
            <el-option label="CET4" value="CET4" />
            <el-option label="CET6" value="CET6" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.paperType">
            <el-option label="真题" value="REAL" />
            <el-option label="模拟" value="SIMULATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="年份"><el-input-number v-model="form.year" :min="2020" :max="2030" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="saving">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getQuestions, createPaper } from '@/api/admin'
import { ElMessage } from 'element-plus'

const papers = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const form = reactive({ title: '', level: 'CET4', paperType: 'REAL', year: 2024 })

async function loadPapers() {
  loading.value = true
  try {
    const res = await getQuestions(1, 999)
    // 从题目列表中展示，试卷从专用接口获取
    papers.value = []
  } catch (e) {}
  loading.value = false
}

async function handleCreate() {
  saving.value = true
  try {
    await createPaper(form)
    ElMessage.success('创建成功')
    showForm.value = false
  } catch (e) {}
  saving.value = false
}

onMounted(loadPapers)
</script>
