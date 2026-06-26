import axios from 'axios';
import { showToast } from 'vant';
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
        showToast({ message: '登录已过期，请重新登录', type: 'fail' });
        const userStore = useUserStore();
        userStore.logout();
        window.location.href = '/login';
      }
      return Promise.reject(new Error(res.msg));
    }
    return res.data;
  },
  error => {
    showToast({ message: error.response?.data?.msg || '网络错误', type: 'fail' });
    return Promise.reject(error);
  }
);

export default request;
