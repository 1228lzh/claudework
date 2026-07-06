<template>
  <div class="my-clues">
    <van-nav-bar title="我的线索" left-text="返回" left-arrow @click-left="router.back" fixed placeholder>
      <template #right>
        <van-icon name="add" size="20" @click="router.push('/submit')" />
      </template>
    </van-nav-bar>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="clues.length === 0 && !refreshing" class="empty-state">
        <van-empty description="暂无提报记录">
          <van-button type="primary" size="small" @click="router.push('/submit')">去提报</van-button>
        </van-empty>
      </div>

      <div v-for="clue in clues" :key="clue.id" class="clue-card tap-active" @click="router.push(`/clue/${clue.id}`)">
        <div class="clue-header">
          <span class="clue-no">{{ clue.clueNo }}</span>
          <span :class="['stage-tag', clue.status]">{{ statusLabel(clue.status) }}</span>
        </div>
        <div class="clue-title">{{ clue.clueName }}</div>
        <div class="clue-meta">
          <span>{{ clue.clueType }}</span>
          <span v-if="clue.submittedAt">{{ formatDate(clue.submittedAt) }}</span>
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyClues } from '../api'
import { showToast } from 'vant'

const router = useRouter()
const clues = ref([])
const refreshing = ref(false)

const statusMap = {
  new: '新建',
  pending_supplement: '待补充',
  initial_screening: '初筛中',
  judging: '研判中',
  verifying: '验证中',
  ipd_review: 'IPD立项',
  initial_screening_rejected: '初筛不通过',
  judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过'
}

function statusLabel(status) {
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)', draft: '草稿(旧)' }
  return statusMap[status] || oldMap[status] || status
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

async function loadClues() {
  try {
    const { data } = await getMyClues()
    if (data.code === 0) {
      clues.value = data.data || []
    }
  } catch (e) {
    console.error('加载失败', e)
  }
}

function onRefresh() {
  loadClues().then(() => {
    refreshing.value = false
  })
}

onMounted(loadClues)
</script>

<style scoped>
.my-clues {
  padding: 46px 0 0;
  min-height: 100vh;
  background: #f5f7fa;
}
.empty-state {
  padding-top: 120px;
}
.clue-card {
  margin: 8px 12px;
  padding: 14px;
  background: #fff;
  border-radius: 8px;
}
.clue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.clue-no {
  font-size: 12px;
  color: #999;
}
.clue-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}
.clue-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>
