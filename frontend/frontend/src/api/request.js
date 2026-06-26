import axios from 'axios';
import { Toast } from 'vant';
import { useUserStore } from '@/stores/user';

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
});

request.interceptors.request.use(config => {
  const userStore = useUserStore();
  if (userStore.token) {
    config.headers.Authorization = 'Bearer ' + userStore.token;
  }
  return config;
}, error => Promise.reject(error));

request.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code !== 200) {
      if (res.code === 401) {
        Toast.fail('登录已过期，请重新登录');
        const userStore = useUserStore();
        userStore.logout();
        window.location.href = '/login';
      }
      return Promise.reject(new Error(res.msg));
    }
    return res.data;
  },
  error => {
    Toast.fail(error.response?.data?.msg || '网络错误');
    return Promise.reject(error);
  }
);

export default request;
