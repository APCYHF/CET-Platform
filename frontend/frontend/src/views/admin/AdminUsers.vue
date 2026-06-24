<template>
  <div>
    <h2>用户管理</h2>
    <el-table :data="users" stripe style="width:100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="账号" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="school" label="学校" />
      <el-table-column prop="role" label="角色" width="80">
        <template #default="{ row }"><el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分" width="60" />
      <el-table-column prop="createdAt" label="注册时间" width="160" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="size" :total="total" layout="prev, pager, next" @current-change="loadUsers" style="margin-top:16px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, updateUserStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUsers(page.value, size.value)
    users.value = res.records
    total.value = res.total
  } catch (e) {}
  loading.value = false
}

async function toggleStatus(row) {
  try {
    await updateUserStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (e) {}
}

onMounted(loadUsers)
</script>
