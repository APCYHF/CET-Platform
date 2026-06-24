<template>
  <div class="login-page">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <div class="login-header">
        <div class="logo-wrap">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="14" fill="#4F6EF7"/>
            <path d="M14 34L24 14L34 34" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M17 28H31" stroke="white" stroke-width="3" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="app-name">CET Prep</h1>
        <p class="app-sub">英语四六级备考平台</p>
      </div>

      <div class="form-card">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-sub">登录继续你的学习之旅</p>

        <div class="input-group">
          <div class="input-label">账号</div>
          <div class="input-wrap" :class="{ focused: focusedField === 'username' }">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="1.8"/>
              <path d="M4 20C4 17.8 7.6 16 12 16C16.4 16 20 17.8 20 20" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入账号"
              @focus="focusedField = 'username'"
              @blur="focusedField = ''"
            />
          </div>
        </div>

        <div class="input-group">
          <div class="input-label">密码</div>
          <div class="input-wrap" :class="{ focused: focusedField === 'password' }">
            <svg class="input-icon" width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="1.8"/>
              <path d="M8 11V7C8 4.79 9.79 3 12 3C14.21 3 16 4.79 16 7V11" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            <input
              v-model="form.password"
              :type="showPwd ? 'text' : 'password'"
              placeholder="请输入密码"
              @focus="focusedField = 'password'"
              @blur="focusedField = ''"
              @keyup.enter="handleLogin"
            />
            <button class="pwd-toggle" @click="showPwd = !showPwd">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <template v-if="showPwd">
                  <path d="M1 12C1 12 5 5 12 5C19 5 23 12 23 12C23 12 19 19 12 19C5 19 1 12 1 12Z" stroke="currentColor" stroke-width="1.8"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.8"/>
                </template>
                <template v-else>
                  <path d="M17.94 17.94A10.07 10.07 0 0112 20C5 20 1 12 1 12A18.45 18.45 0 015.06 5.06M9.9 4.24A9.12 9.12 0 0112 4C19 4 23 12 23 12A18.5 18.5 0 0120.59 15.59" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                  <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                </template>
              </svg>
            </button>
          </div>
        </div>

        <button class="btn-login" @click="handleLogin" :disabled="loading">
          <span v-if="!loading">登 录</span>
          <span v-else class="loading-dots">
            <span></span><span></span><span></span>
          </span>
        </button>

        <div class="divider"><span>还没有账号？</span></div>
        <button class="btn-register" @click="$router.push('/register')">立即注册</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const showPwd = ref(false)
const focusedField = ref('')
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) {
    showToast('请填写账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.user)
    showToast({ message: '登录成功 👋', type: 'success' })
    router.push('/home')
  } catch (e) {}
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(145deg, #EEF1FE 0%, #F4F6FB 50%, #E8F5EE 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.5;
}
.circle-1 { width: 300px; height: 300px; background: #4F6EF7; top: -100px; right: -80px; opacity: 0.12; }
.circle-2 { width: 200px; height: 200px; background: #06C073; bottom: 100px; left: -60px; opacity: 0.12; }
.circle-3 { width: 150px; height: 150px; background: #FFAB30; bottom: -50px; right: 50px; opacity: 0.1; }

.login-content {
  width: 100%;
  max-width: 420px;
  padding: 0 24px 40px;
  position: relative;
  z-index: 1;
}

.login-header { text-align: center; padding: 48px 0 32px; }
.logo-wrap { display: inline-block; margin-bottom: 16px; filter: drop-shadow(0 8px 24px rgba(79,110,247,0.3)); }
.app-name { margin: 0; font-size: 28px; font-weight: 800; color: #1A1D2E; letter-spacing: -0.5px; }
.app-sub { margin: 6px 0 0; color: #6B7280; font-size: 14px; }

.form-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px 28px;
  box-shadow: 0 8px 40px rgba(79,110,247,0.12);
}

.form-title { margin: 0 0 4px; font-size: 22px; font-weight: 700; color: #1A1D2E; }
.form-sub { margin: 0 0 28px; color: #9CA3AF; font-size: 14px; }

.input-group { margin-bottom: 20px; }
.input-label { font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 8px; }

.input-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1.5px solid #E5E7EB;
  border-radius: 12px;
  padding: 12px 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #F9FAFB;
}
.input-wrap.focused {
  border-color: #4F6EF7;
  box-shadow: 0 0 0 3px rgba(79,110,247,0.12);
  background: #fff;
}
.input-icon { color: #9CA3AF; flex-shrink: 0; transition: color 0.2s; }
.input-wrap.focused .input-icon { color: #4F6EF7; }

.input-wrap input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: #1A1D2E;
  background: transparent;
}
.input-wrap input::placeholder { color: #D1D5DB; }

.pwd-toggle {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: #9CA3AF;
  display: flex;
  align-items: center;
}

.btn-login {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #4F6EF7, #6B87FF);
  color: #fff;
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 2px;
  margin-top: 8px;
  box-shadow: 0 4px 16px rgba(79,110,247,0.4);
  transition: transform 0.15s, box-shadow 0.15s;
}
.btn-login:active { transform: scale(0.98); box-shadow: 0 2px 8px rgba(79,110,247,0.3); }
.btn-login:disabled { opacity: 0.7; }

.loading-dots { display: flex; gap: 6px; justify-content: center; align-items: center; }
.loading-dots span {
  width: 8px; height: 8px; border-radius: 50%; background: #fff;
  animation: bounce 1.2s infinite;
}
.loading-dots span:nth-child(2) { animation-delay: 0.2s; }
.loading-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { 0%, 80%, 100% { transform: scale(0.7); opacity: 0.4; } 40% { transform: scale(1); opacity: 1; } }

.divider {
  text-align: center;
  color: #9CA3AF;
  font-size: 13px;
  margin: 20px 0 12px;
}

.btn-register {
  width: 100%;
  height: 46px;
  background: transparent;
  color: #4F6EF7;
  border: 1.5px solid #4F6EF7;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-register:active { background: #EEF1FE; }
</style>