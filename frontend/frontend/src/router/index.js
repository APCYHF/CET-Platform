import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/user/Home.vue'),
    meta: { title: '首页', showNav: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', showNav: true }
  },
  {
    path: '/scores',
    name: 'Scores',
    component: () => import('@/views/user/Scores.vue'),
    meta: { title: '成绩查询', showNav: true }
  },
  {
    path: '/practice/reading',
    name: 'ReadingPractice',
    component: () => import('@/views/user/ReadingPractice.vue'),
    meta: { title: '阅读练习', showNav: true }
  },
  {
    path: '/practice/listening',
    name: 'ListeningPractice',
    component: () => import('@/views/user/ListeningPractice.vue'),
    meta: { title: '听力练习', showNav: true }
  },
  {
    path: '/practice/reading/:id',
    name: 'ReadingDetail',
    component: () => import('@/views/user/ReadingDetail.vue'),
    meta: { title: '阅读答题' }
  },
  {
    path: '/practice/listening/:id',
    name: 'ListeningDetail',
    component: () => import('@/views/user/ListeningDetail.vue'),
    meta: { title: '听力答题' }
  },
  {
    path: '/composition',
    name: 'Composition',
    component: () => import('@/views/user/Composition.vue'),
    meta: { title: '写作翻译', showNav: true }
  },
  {
    path: '/vocabulary',
    name: 'Vocabulary',
    component: () => import('@/views/user/Vocabulary.vue'),
    meta: { title: '生词本', showNav: true }
  },
  {
    path: '/study',
    name: 'Study',
    component: () => import('@/views/user/Study.vue'),
    meta: { title: '背单词', showNav: true }
  },
  {
    path: '/mall',
    name: 'Mall',
    component: () => import('@/views/user/Mall.vue'),
    meta: { title: '商城', showNav: true }
  },
  {
    path: '/mall/orders',
    name: 'MyOrders',
    component: () => import('@/views/user/MyOrders.vue'),
    meta: { title: '我的订单' }
  },
  {
    path: '/paper/:id',
    name: 'PaperExam',
    component: () => import('@/views/user/PaperExam.vue'),
    meta: { title: '整套真题模拟' }
  },
  {
    path: '/paper/:id/result',
    name: 'PaperResult',
    component: () => import('@/views/user/PaperResult.vue'),
    meta: { title: '考试结果' }
  },
  {
    path: '/wrong',
    name: 'WrongBook',
    component: () => import('@/views/user/WrongBook.vue'),
    meta: { title: '错题本', showNav: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.title) {
    document.title = to.meta.title + ' - CET Prep'
  }
  if (!userStore.token && to.name !== 'Login' && to.name !== 'Register') {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router
