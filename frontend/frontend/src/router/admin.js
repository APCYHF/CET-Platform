import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/admin/dashboard'
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { title: '管理登录' }
  },
  {
    path: '/admin/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '数据看板', icon: 'Odometer' }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/admin/AdminUsers.vue'),
    meta: { title: '用户管理', icon: 'User' }
  },
  {
    path: '/admin/questions',
    name: 'AdminQuestions',
    component: () => import('@/views/admin/AdminQuestions.vue'),
    meta: { title: '题库管理', icon: 'Notebook' }
  },
  {
    path: '/admin/papers',
    name: 'AdminPapers',
    component: () => import('@/views/admin/AdminPapers.vue'),
    meta: { title: '试卷管理', icon: 'Document' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('@/views/admin/AdminOrders.vue'),
    meta: { title: '订单管理', icon: 'List' }
  },
  {
    path: '/admin/corrections',
    name: 'AdminCorrections',
    component: () => import('@/views/admin/AdminCorrections.vue'),
    meta: { title: '批改管理', icon: 'Edit' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.title) {
    document.title = to.meta.title + ' - 管理后台'
  }
  if (to.name !== 'AdminLogin' && !userStore.token) {
    next({ name: 'AdminLogin' })
  } else if (to.name === 'AdminLogin' && userStore.token) {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router
