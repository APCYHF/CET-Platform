<template>
  <el-container style="min-height: 100vh">
    <el-aside v-if="showSidebar" :width="isCollapse ? '64px' : '220px'">
      <div class="admin-logo">{{ isCollapse ? 'CET' : 'CET管理后台' }}</div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409eff"
        router
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.meta?.icon" /></el-icon>
          <span>{{ item.meta?.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header v-if="showSidebar">
        <div style="display:flex;align-items:center;gap:12px">
          <el-button @click="isCollapse = !isCollapse" text>
            <el-icon><Fold /></el-icon>
          </el-button>
          <span style="font-size:14px;color:#666">四六级服务平台管理后台</span>
        </div>
        <div>
          <el-button type="text" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Fold } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const showSidebar = computed(() => route.name !== 'AdminLogin')

const menus = [
  { path: '/admin/dashboard', meta: { title: '数据看板', icon: 'Odometer' } },
  { path: '/admin/users', meta: { title: '用户管理', icon: 'User' } },
  { path: '/admin/questions', meta: { title: '题库管理', icon: 'Notebook' } },
  { path: '/admin/papers', meta: { title: '试卷管理', icon: 'Document' } },
  { path: '/admin/orders', meta: { title: '订单管理', icon: 'List' } },
  { path: '/admin/corrections', meta: { title: '批改管理', icon: 'Edit' } }
]

function logout() {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style>
body { margin: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
.el-aside { background: #001529; transition: width 0.3s; }
.admin-logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; letter-spacing: 2px; }
.el-header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #eee; padding: 0 20px; }
.el-menu { border-right: none; }
</style>
