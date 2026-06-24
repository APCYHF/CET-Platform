<template>
  <div id="user-app">
    <router-view />
    <van-tabbar
      v-if="showTabbar"
      v-model="activeTab"
      active-color="#4F6EF7"
      inactive-color="#9CA3AF"
      safe-area-inset-bottom
      route
    >
      <van-tabbar-item to="/home">
        <template #icon="{ active }">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M3 12L12 4L21 12V21H15V15H9V21H3V12Z" :fill="active ? '#4F6EF7' : 'none'" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linejoin="round"/>
          </svg>
        </template>
        首页
      </van-tabbar-item>
      <van-tabbar-item to="/practice/reading">
        <template #icon="{ active }">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <rect x="3" y="4" width="18" height="16" rx="2" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8"/>
            <line x1="7" y1="9" x2="17" y2="9" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="7" y1="13" x2="14" y2="13" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </template>
        刷题
      </van-tabbar-item>
      <van-tabbar-item to="/study">
        <template #icon="{ active }">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </template>
        背单词
      </van-tabbar-item>
      <van-tabbar-item to="/mall">
        <template #icon="{ active }">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <path d="M6 2L3 6V20C3 21.1 3.9 22 5 22H19C20.1 22 21 21.1 21 20V6L18 2H6Z" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linejoin="round"/>
            <line x1="3" y1="6" x2="21" y2="6" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8"/>
            <path d="M16 10C16 12.21 14.21 14 12 14C9.79 14 8 12.21 8 10" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </template>
        商城
      </van-tabbar-item>
      <van-tabbar-item to="/profile">
        <template #icon="{ active }">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="8" r="4" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8"/>
            <path d="M4 20C4 17.8 7.6 16 12 16C16.4 16 20 17.8 20 20" :stroke="active ? '#4F6EF7' : '#9CA3AF'" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
        </template>
        我的
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref(0)

const tabbarRoutes = ['/home', '/practice/reading', '/study', '/mall', '/profile']
const showTabbar = computed(() => {
  const path = route.path
  return tabbarRoutes.some(r => path.startsWith(r)) || path === '/home'
})
</script>

<style>
:root {
  --color-primary: #4F6EF7;
  --color-primary-light: #EEF1FE;
  --color-secondary: #06C073;
  --color-danger: #F05656;
  --color-warning: #FFAB30;
  --color-bg: #F4F6FB;
  --color-card: #FFFFFF;
  --color-text: #1A1D2E;
  --color-text-secondary: #6B7280;
  --color-border: #E5E7EB;
  --radius-sm: 10px;
  --radius-md: 16px;
  --radius-lg: 24px;
  --shadow-sm: 0 1px 4px rgba(79,110,247,0.06);
  --shadow-md: 0 4px 20px rgba(79,110,247,0.10);
  --shadow-lg: 0 8px 40px rgba(79,110,247,0.15);
}

* { box-sizing: border-box; }

body {
  margin: 0;
  background: var(--color-bg);
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  color: var(--color-text);
  -webkit-font-smoothing: antialiased;
}

/* Vant overrides */
:root {
  --van-primary-color: #4F6EF7;
  --van-success-color: #06C073;
  --van-danger-color: #F05656;
  --van-warning-color: #FFAB30;
  --van-nav-bar-background: #ffffff;
  --van-nav-bar-height: 52px;
  --van-tabbar-height: 58px;
  --van-tabbar-background: #ffffff;
  --van-cell-background: #ffffff;
  --van-background: #F4F6FB;
  --van-background-2: #F4F6FB;
}

.van-nav-bar {
  border-bottom: 1px solid var(--color-border) !important;
  box-shadow: none !important;
}

.van-nav-bar__title {
  font-size: 17px !important;
  font-weight: 600 !important;
  color: var(--color-text) !important;
}

.van-tabbar {
  border-top: 1px solid var(--color-border) !important;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05) !important;
}

.van-tabbar-item__text {
  font-size: 11px !important;
  margin-top: 2px !important;
}

.van-button--primary {
  background: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
}

.van-button--round {
  border-radius: 50px !important;
}

.van-cell-group--inset {
  border-radius: var(--radius-md) !important;
  overflow: hidden;
  box-shadow: var(--shadow-sm) !important;
}

/* Page transition */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>