<template>
  <div class="admin-list">
    <van-nav-bar title="线索审核管理" fixed placeholder>
      <template #left>
        <span class="nav-title">管理员</span>
      </template>
    </van-nav-bar>

    <!-- 筛选标签 -->
    <div class="filter-bar">
      <van-tabs v-model:active="activeTab" @change="onTabChange" swipeable>
        <van-tab v-for="tab in tabs" :key="tab.key" :title="tab.label" />
      </van-tabs>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="clues.length === 0 && !refreshing" class="empty-state">
        <van-empty :description="activeTab === 'all' ? '暂无线索' : '该阶段暂无待审核线索'" />
      </div>

      <div v-for="clue in clues" :key="clue.id" class="clue-card tap-active" @click="router.push(`/admin/clue/${clue.id}`)">
        <div class="clue-header">
          <span class="clue-no">{{ clue.clueNo }}</span>
          <span :class="['stage-tag', clue.status]">{{ statusLabel(clue.status) }}</span>
        </div>
        <div class="clue-title">{{ clue.clueName }}</div>
        <div class="clue-meta">
          <span>{{ clue.reporterName }} / {{ clue.reporterDept }}</span>
          <span v-if="clue.submittedAt">{{ formatDate(clue.submittedAt) }}</span>
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReviewClues } from '../api'

const router = useRouter()
const clues = ref([])
const refreshing = ref(false)
const activeTab = ref(0)

const tabs = [
  { key: '', label: '全部' },
  { key: 'initial_screening', label: '初筛中' },
  { key: 'judging', label: '研判中' },
  { key: 'verifying', label: '验证中' },
  { key: 'ipd_review', label: 'IPD立项' }
]

const statusMap = {
  new: '新建',
  initial_screening: '初筛中',
  judging: '研判中',
  verifying: '验证中',
  ipd_review: 'IPD立项',
  initial_screening_rejected: '初筛不通过',
  judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过'
}

function statusLabel(s) {
  // 兼容旧数据状态
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)', draft: '草稿(旧)' }
  return statusMap[s] || oldMap[s] || s
}
function formatDate(d) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }

async function loadClues() {
  try {
    const tab = tabs[activeTab.value]
    const status = tab.key === 'all' ? '' : tab.key
    const { data } = await getReviewClues(status)
    if (data.code === 0) {
      clues.value = data.data || []
    }
  } catch (e) {
    console.error('加载失败', e)
  }
}

function onTabChange() { loadClues() }
function onRefresh() { loadClues().then(() => { refreshing.value = false }) }

onMounted(loadClues)
</script>

<style scoped>
.admin-list {
  padding: 46px 0 0;
  min-height: 100vh;
  background: #f5f7fa;
}
.nav-title { font-size: 14px; font-weight: 500; }
.filter-bar {
  background: #fff;
  border-bottom: 1px solid #ebedf0;
}
.empty-state { padding-top: 80px; }
.clue-card {
  margin: 8px 12px;
  padding: 14px;
  background: #fff;
  border-radius: 8px;
  border-left: 3px solid #1989fa;
}
.clue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.clue-no { font-size: 12px; color: #999; }
.clue-title { font-size: 15px; font-weight: 500; margin-bottom: 8px; }
.clue-meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; }
</style>
