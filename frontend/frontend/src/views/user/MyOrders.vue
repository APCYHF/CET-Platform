<template>
  <div class="orders-page">
    <van-nav-bar title="我的订单" left-arrow @click-left="$router.back()" />
    <van-cell-group inset style="margin:12px">
      <van-cell v-for="o in orders" :key="o.id">
        <template #title>
          <span>{{ o.orderNo }}</span>
          <van-tag :type="statusTag(o.status)" style="margin-left:8px">{{ statusLabel(o.status) }}</van-tag>
        </template>
        <template #label>
          <p>金额: ¥{{ o.amount }} | 支付: {{ o.payType }}</p>
          <p v-if="o.payTime">支付时间: {{ o.payTime }}</p>
        </template>
      </van-cell>
      <van-empty v-if="!orders.length" description="暂无订单" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOrders } from '@/api/mall'

const orders = ref([])

function statusTag(s) {
  return { UNPAID: 'danger', PAID: 'primary', SHIPPED: 'warning', COMPLETED: 'success', CANCELLED: 'default' }[s] || 'default'
}
function statusLabel(s) {
  return { UNPAID: '未支付', PAID: '已支付', SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s
}

onMounted(async () => {
  try { orders.value = await getMyOrders() } catch (e) {}
})
</script>
