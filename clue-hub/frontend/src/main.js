import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css'
import 'vant/es/toast/index.css'
import 'vant/es/dialog/index.css'
import 'vant/es/popup/index.css'
import 'vant/es/loading/index.css'
import 'vant/es/icon/index.css'
import './assets/common.css'

const app = createApp(App)
app.use(router)
app.mount('#app')
