<template>
  <div :class="['app-container', { admin: isAdminRoute }]">
    <router-view v-if="userStore.loaded" />
    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { userStore } from './stores/user.js'
import { getCurrentUser } from './api/auth.js'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

onMounted(async () => {
  try {
    const { data } = await getCurrentUser()
    userStore.setUser(data)
  } catch (e) {
    if (e.response && e.response.status === 401) {
      window.location.href = '/auth/login'
    }
  }
})
</script>
