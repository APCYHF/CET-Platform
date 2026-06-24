<template>
  <div class="admin-login">
    <div class="login-box">
      <h2>CET 管理后台</h2>
      <el-form :model="form" :rules="rules" ref="formRef" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const rules = { username: [{ required: true, message: '请输入账号' }], password: [{ required: true, message: '请输入密码' }] }

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.user)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (e) {}
  loading.value = false
}
</script>

<style scoped>
.admin-login { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea, #764ba2); }
.login-box { background: #fff; padding: 40px; border-radius: 12px; width: 360px; box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
.login-box h2 { text-align: center; margin-bottom: 30px; color: #333; }
</style>
