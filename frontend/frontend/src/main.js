import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';

// Vant 移动端UI
import { Button, NavBar, Tabbar, TabbarItem, Card, Field, Form, Cell, CellGroup, Popup, Toast, Dialog, Icon, List, PullRefresh, SwipeCell, Tag, Progress, Slider, NoticeBar, Uploader } from 'vant';
import 'vant/lib/index.css';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);

// 注册常用 Vant 组件
const vantComponents = [Button, NavBar, Tabbar, TabbarItem, Card, Field, Form, Cell, CellGroup, Popup, Toast, Dialog, Icon, List, PullRefresh, SwipeCell, Tag, Progress, Slider, NoticeBar, Uploader];
vantComponents.forEach(c => app.use(c));

app.mount('#app');
