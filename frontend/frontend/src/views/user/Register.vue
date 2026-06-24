<template>
  <div class="register-page">
    <van-nav-bar title="注册" left-arrow @click-left="$router.back()" />
    <van-form @submit="handleRegister">
      <van-cell-group inset>
        <van-field v-model="form.username" label="账号" placeholder="4-20位字母或数字" :rules="[{ required: true, message: '请输入账号' }, { pattern: /^[a-zA-Z0-9]{4,20}$/, message: '账号格式不正确' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="6-20位" :rules="[{ required: true, message: '请输入密码' }, { pattern: /^.{6,20}$/, message: '密码长度6-20位' }]" />
        <van-field v-model="form.nickname" label="昵称" placeholder="选填" />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">注册</van-button>
      </div>
    </van-form>
    <div class="footer"><span @click="$router.push('/login')">已有账号？去登录</span></div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { showToast } from 'vant'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '' })

async function handleRegister() {
  loading.value = true
  try {
    await register(form)
    showToast('注册成功')
    router.push('/login')
  } catch (e) {} finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page { min-height: 100vh; background: #fff; }
.footer { text-align: center; color: #1989fa; font-size: 14px; }
</style>
