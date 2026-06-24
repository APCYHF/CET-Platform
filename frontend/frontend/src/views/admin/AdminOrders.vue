<template>
  <div>
    <h2>订单管理</h2>
    <el-table :data="orders" stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="amount" label="金额" width="80" />
      <el-table-column prop="payType" label="支付方式" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'UNPAID'" size="small" @click="updateStatus(row, 'CANCELLED')">取消</el-button>
          <el-button v-if="row.status === 'PAID'" size="small" type="primary" @click="updateStatus(row, 'SHIPPED')">发货</el-button>
          <el-button v-if="row.status === 'SHIPPED'" size="small" type="success" @click="updateStatus(row, 'COMPLETED')">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrders, updateOrderStatus } from '@/api/admin'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)

function statusType(s) {
  return { UNPAID: 'danger', PAID: 'primary', SHIPPED: 'warning', COMPLETED: 'success', CANCELLED: 'info' }[s] || 'default'
}
function statusLabel(s) {
  return { UNPAID: '未支付', PAID: '已支付', SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s
}

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrders(1, 50)
    orders.value = res.records || []
  } catch (e) {}
  loading.value = false
}

async function updateStatus(row, status) {
  try {
    await updateOrderStatus(row.id, status)
    ElMessage.success('操作成功')
    loadOrders()
  } catch (e) {}
}

onMounted(loadOrders)
</script>
