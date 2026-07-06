<template>
  <div class="admin-home">
    <!-- 线索管理 Tab -->
    <div v-show="activeTab === 'clues'" class="tab-content">
      <van-nav-bar title="线索管理" fixed />

      <div class="filter-bar">
        <van-tabs v-model:active="activeFilter" @change="onFilterChange" swipeable>
          <van-tab v-for="tab in tabs" :key="tab.key" :title="tab.label" />
        </van-tabs>
      </div>

      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-if="clues.length === 0 && !refreshing" class="empty-state">
          <van-empty :description="activeFilter === '' ? '暂无线索' : '该阶段暂无待审核线索'" />
        </div>

        <div v-else>
          <div v-for="clue in clues" :key="clue.id" class="clue-card tap-active"
            @click="router.push(`/admin/clue/${clue.id}`)">
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
        </div>
      </van-pull-refresh>
    </div>

    <!-- 我的 Tab -->
    <div v-show="activeTab === 'mine'" class="tab-content">
      <van-nav-bar title="我的" fixed />

      <div class="profile-card">
        <div class="avatar">
          <van-icon name="user-circle-o" size="60" color="#1989fa" />
        </div>
        <div class="profile-info">
          <div class="name">{{ userStore.fullname || '审核员' }}</div>
          <div class="dept">工号：{{ userStore.userId || '未知' }}</div>
        </div>
      </div>

      <van-cell-group title="统计">
        <van-cell title="累计审核" :value="totalCount + ' 条'" />
        <van-cell title="待审核" :value="pendingCount + ' 条'" />
        <van-cell title="已立项" :value="ipdCount + ' 条'" />
      </van-cell-group>
    </div>

    <!-- 底部导航栏 -->
    <van-tabbar v-model="activeTab" fixed placeholder safe-area-inset-bottom>
      <van-tabbar-item name="clues" icon="notes-o">线索管理</van-tabbar-item>
      <van-tabbar-item name="mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReviewClues } from '../api'
import { userStore } from '../stores/user.js'

const router = useRouter()
const activeTab = ref('clues')
const clues = ref([])
const refreshing = ref(false)
const activeFilter = ref(0)

const tabs = [
  { key: '', label: '全部' },
  { key: 'initial_screening', label: '初筛中' },
  { key: 'judging', label: '研判中' },
  { key: 'verifying', label: '验证中' },
  { key: 'ipd_review', label: 'IPD立项' }
]

const statusMap = {
  new: '新建',
  pending_supplement: '待补充',
  initial_screening: '初筛中', judging: '研判中', verifying: '验证中',
  ipd_review: 'IPD立项',
  initial_screening_rejected: '初筛不通过', judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过'
}

const totalCount = computed(() => clues.value.length)
const pendingCount = computed(() =>
  clues.value.filter(c => ['initial_screening', 'judging', 'verifying'].includes(c.status)).length
)
const ipdCount = computed(() =>
  clues.value.filter(c => c.status === 'ipd_review').length
)

function statusLabel(s) {
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)' }
  return statusMap[s] || oldMap[s] || s
}
function formatDate(d) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }

async function loadClues() {
  try {
    const tab = tabs[activeFilter.value]
    const status = tab.key || ''
    const { data } = await getReviewClues(status)
    if (data.code === 0) clues.value = data.data || []
  } catch (e) { /* ignore */ }
}

function onFilterChange() { loadClues() }
function onRefresh() { loadClues().then(() => { refreshing.value = false }) }

onMounted(loadClues)
</script>

<style scoped>
.admin-home {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 110px;
}
.tab-content { padding-top: 46px; }
.filter-bar {
  background: #fff;
  border-bottom: 1px solid #ebedf0;
}
.empty-state {
  padding-top: 80px;
}

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
.clue-title { font-size: 15px; font-weight: 500; margin-bottom: 8px; color: #333; }
.clue-meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; }

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 16px;
  background: #fff;
  margin-bottom: 12px;
}
.profile-info { flex: 1; min-width: 0; }
.profile-info .name { font-size: 18px; font-weight: 600; color: #333; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.profile-info .dept { font-size: 14px; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
