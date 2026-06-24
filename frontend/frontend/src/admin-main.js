import { createApp } from 'vue';
import AdminApp from './AdminApp.vue';
import router from './router/admin';
import { createPinia } from 'pinia';

// Element Plus 管理端UI
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

const app = createApp(AdminApp);
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(ElementPlus);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.mount('#app');
