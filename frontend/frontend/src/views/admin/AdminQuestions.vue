<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
      <h2 style="margin:0">题库管理</h2>
      <el-button type="primary" @click="showForm = true">添加题目</el-button>
    </div>

    <el-table :data="questions" stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="type" label="题型" width="100">
        <template #default="{ row }"><el-tag>{{ row.type }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="subType" label="子类型" width="120" />
      <el-table-column prop="content" label="题干" min-width="200" show-overflow-tooltip />
      <el-table-column prop="answer" label="答案" width="80" />
      <el-table-column prop="difficulty" label="难度" width="60" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleExtract(row)">提取词汇</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadQuestions" style="margin-top:16px" />

    <el-dialog v-model="showForm" title="添加题目" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="题型">
          <el-select v-model="form.type">
            <el-option label="READING" value="READING" />
            <el-option label="LISTENING" value="LISTENING" />
            <el-option label="WRITING" value="WRITING" />
            <el-option label="TRANSLATION" value="TRANSLATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="子类型"><el-input v-model="form.subType" /></el-form-item>
        <el-form-item label="题干"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="答案"><el-input v-model="form.answer" /></el-form-item>
        <el-form-item label="解析"><el-input v-model="form.analysis" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="难度"><el-input-number v-model="form.difficulty" :min="1" :max="5" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getQuestions, addQuestion, deleteQuestion, extractWords } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const questions = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const form = reactive({ type: 'READING', subType: '', content: '', answer: '', analysis: '', difficulty: 3 })

async function loadQuestions() {
  loading.value = true
  try {
    const res = await getQuestions(page.value, size.value)
    questions.value = res.records
    total.value = res.total
  } catch (e) {}
  loading.value = false
}

async function handleAdd() {
  saving.value = true
  try {
    await addQuestion(form)
    ElMessage.success('添加成功')
    showForm.value = false
    loadQuestions()
  } catch (e) {}
  saving.value = false
}

async function handleDelete(row) {
  ElMessageBox.confirm('确定删除该题目？').then(async () => {
    await deleteQuestion(row.id)
    ElMessage.success('已删除')
    loadQuestions()
  }).catch(() => {})
}

async function handleExtract(row) {
  try {
    const res = await extractWords(row.id)
    ElMessage.success('提取完成: ' + (res?.message || '成功'))
  } catch (e) {}
}

onMounted(loadQuestions)
</script>
