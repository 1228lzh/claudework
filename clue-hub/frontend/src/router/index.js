import { createRouter, createWebHistory } from 'vue-router'
import { userStore } from '../stores/user.js'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/UserHome.vue'),
    meta: { title: '线索管理' }
  },
  {
    path: '/submit',
    name: 'SubmitForm',
    component: () => import('../views/SubmitForm.vue'),
    meta: { title: '新建线索', hideTabbar: true }
  },
  {
    path: '/clue/:id',
    name: 'ClueDetail',
    component: () => import('../views/ClueDetail.vue'),
    meta: { title: '线索详情', hideTabbar: true }
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('../views/AdminHome.vue'),
    meta: { title: '线索管理' }
  },
  {
    path: '/admin/clue/:id',
    name: 'AdminDetail',
    component: () => import('../views/AdminDetail.vue'),
    meta: { title: '审核详情', hideTabbar: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path.startsWith('/admin') && !userStore.admin) {
    return '/'
  }
})

router.afterEach((to) => {
  document.title = to.meta.title || '市场机会线索提报'
})

export default router
