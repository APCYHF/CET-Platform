<template>
  <div class="mall-page">
    <div class="mall-header">
      <button class="back-btn" @click="$router.back()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M19 12H5M12 5L5 12L12 19" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
      <h2>资料商城</h2>
      <button class="orders-btn" @click="$router.push('/mall/orders')">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
          <path d="M9 5H7C5.9 5 5 5.9 5 7V19C5 20.1 5.9 21 7 21H17C18.1 21 19 20.1 19 19V7C19 5.9 18.1 5 17 5H15" stroke="white" stroke-width="1.8"/>
          <rect x="9" y="3" width="6" height="4" rx="1" stroke="white" stroke-width="1.8"/>
          <line x1="9" y1="12" x2="15" y2="12" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
          <line x1="9" y1="16" x2="13" y2="16" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </button>
    </div>

    <!-- Filter Tabs -->
    <div class="filter-tabs">
      <button
        v-for="tab in tabs" :key="tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >{{ tab.label }}</button>
    </div>

    <!-- Product Grid -->
    <div class="products-grid">
      <div
        v-for="p in filteredProducts" :key="p.id"
        class="product-card"
      >
        <div class="product-img">
          <img :src="p.cover || 'https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg'" :alt="p.name" />
          <div class="product-type-badge" :class="'type-' + p.type">{{ typeLabel(p.type) }}</div>
          <div v-if="p.stock < 10" class="stock-badge">仅剩{{ p.stock }}</div>
        </div>
        <div class="product-info">
          <h4 class="product-name">{{ p.name }}</h4>
          <p class="product-desc">{{ p.description }}</p>
          <div class="product-footer">
            <div class="price-area">
              <span class="price-main">¥{{ p.price }}</span>
              <span class="price-points">{{ p.points }} 积分</span>
            </div>
            <div class="buy-btns">
              <button class="btn-points" @click="handleBuy(p, 'POINTS')">积分换</button>
              <button class="btn-buy" @click="handleBuy(p, 'WECHAT')">购买</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!filteredProducts.length" class="empty-state">
      <div class="empty-icon">🛒</div>
      <p>该分类暂无商品</p>
    </div>
    <div style="height:80px"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProducts, createOrder } from '@/api/mall'
import { showToast } from 'vant'

const products = ref([])
const activeTab = ref('ALL')

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '实体', value: 'PHYSICAL' },
  { label: '电子', value: 'DIGITAL' },
  { label: '课程', value: 'COURSE' },
]

const filteredProducts = computed(() => {
  if (activeTab.value === 'ALL') return products.value
  return products.value.filter(p => p.type === activeTab.value)
})

function typeLabel(t) {
  return { PHYSICAL: '实体', DIGITAL: '电子', COURSE: '课程' }[t] || t
}

async function handleBuy(product, payType) {
  try {
    await createOrder(product.id, payType)
    showToast({ message: '购买成功 🎉', type: 'success' })
  } catch (e) {}
}

onMounted(async () => {
  try { products.value = await getProducts() } catch (e) {}
})
</script>

<style scoped>
.mall-page { min-height: 100vh; background: #F4F6FB; }

.mall-header {
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  padding: 52px 20px 20px;
  display: flex; align-items: center; gap: 12px;
}
.back-btn, .orders-btn {
  width: 36px; height: 36px; border-radius: 50%;
  border: none; background: rgba(255,255,255,0.2); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
}
.mall-header h2 { flex: 1; color: #fff; font-size: 18px; font-weight: 700; margin: 0; text-align: center; }

.filter-tabs {
  display: flex; gap: 8px; padding: 16px 16px 8px;
  overflow-x: auto; scrollbar-width: none;
}
.filter-tabs::-webkit-scrollbar { display: none; }
.tab-btn {
  padding: 8px 18px; border-radius: 50px; border: 1.5px solid #E5E7EB;
  background: #fff; font-size: 14px; font-weight: 500; color: #6B7280;
  cursor: pointer; white-space: nowrap; transition: all 0.2s;
}
.tab-btn.active { background: #4F6EF7; border-color: #4F6EF7; color: #fff; }

.products-grid { padding: 8px 16px 0; display: flex; flex-direction: column; gap: 12px; }

.product-card {
  background: #fff; border-radius: 16px; overflow: hidden;
  box-shadow: 0 2px 8px rgba(79,110,247,0.06);
  display: flex;
}
.product-img {
  width: 100px; height: 100px; flex-shrink: 0; position: relative; overflow: hidden;
}
.product-img img { width: 100%; height: 100%; object-fit: cover; }
.product-type-badge {
  position: absolute; top: 6px; left: 6px;
  font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 4px;
}
.type-PHYSICAL { background: #EEF1FE; color: #4F6EF7; }
.type-DIGITAL { background: #F0FDF4; color: #06C073; }
.type-COURSE { background: #FFFBEB; color: #D97706; }
.stock-badge {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(240,86,86,0.85); color: #fff;
  font-size: 11px; text-align: center; padding: 3px;
}

.product-info { flex: 1; padding: 12px 14px; display: flex; flex-direction: column; }
.product-name { font-size: 15px; font-weight: 600; color: #1A1D2E; margin: 0 0 4px; }
.product-desc { font-size: 12px; color: #9CA3AF; margin: 0 0 10px; line-height: 1.4; flex: 1; }

.product-footer { display: flex; align-items: center; justify-content: space-between; }
.price-area { display: flex; flex-direction: column; }
.price-main { font-size: 18px; font-weight: 800; color: #F05656; }
.price-points { font-size: 11px; color: #9CA3AF; margin-top: 2px; }

.buy-btns { display: flex; gap: 6px; }
.btn-points, .btn-buy {
  padding: 6px 12px; border-radius: 8px; border: none; cursor: pointer;
  font-size: 12px; font-weight: 600; transition: opacity 0.15s;
}
.btn-points { background: #EEF1FE; color: #4F6EF7; }
.btn-buy { background: #4F6EF7; color: #fff; }

.empty-state { padding: 60px 0; text-align: center; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-state p { color: #9CA3AF; }
</style>