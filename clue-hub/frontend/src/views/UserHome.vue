<template>
  <div class="user-home">
    <!-- 线索提报 Tab -->
    <div v-show="activeTab === 'clues'" class="tab-content">
      <van-nav-bar title="线索提报" fixed>
        <template #right>
          <van-icon name="add-o" size="22" @click="router.push('/submit?new=true')" />
        </template>
      </van-nav-bar>

      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <div v-if="clues.length === 0 && !refreshing" class="empty-state">
          <van-empty description="暂无提报记录">
            <van-button type="primary" size="small" @click="router.push('/submit?new=true')">新建线索</van-button>
          </van-empty>
        </div>

        <div v-else>
          <div v-for="clue in clues" :key="clue.id" class="clue-card tap-active"
            @click="goToClue(clue)">
            <div class="clue-header">
              <span class="clue-no">{{ clue.clueNo }}</span>
              <div>
                <span :class="['stage-tag', clue.status]">{{ statusLabel(clue.status) }}</span>
                <div v-if="clue.status === 'initial_screening' || clue.status === 'new'" class="clue-actions">
                  <span v-if="clue.status === 'initial_screening'" class="action-btn"
                    @click.stop="onWithdraw(clue)">撤回</span>
                  <span v-if="clue.status === 'new'" class="action-btn"
                    @click.stop="onDelete(clue)">删除</span>
                </div>
              </div>
            </div>
            <div class="clue-title">{{ clue.clueName }}</div>
            <div class="clue-meta">
              <span>{{ formatDate(clue.submittedAt) }}</span>
            </div>
          </div>
        </div>
      </van-pull-refresh>

      <!-- 新建线索浮动按钮 -->
      <div class="fab-wrapper safe-bottom">
        <van-button type="primary" icon="plus" round block @click="router.push('/submit?new=true')">
          新建线索
        </van-button>
      </div>
    </div>

    <!-- 我的 Tab -->
    <div v-show="activeTab === 'mine'" class="tab-content">
      <van-nav-bar title="我的" fixed />

      <div class="profile-card">
        <div class="avatar">
          <van-icon name="user-circle-o" size="60" color="#1989fa" />
        </div>
        <div class="profile-info">
          <div class="name">{{ userStore.fullname || '未设置' }}</div>
          <div class="contact">工号：{{ userStore.userId || '未知' }}</div>
        </div>
      </div>

      <van-cell-group title="统计">
        <van-cell title="累计提报" :value="clues.length + ' 条'" />
        <van-cell title="初筛中" :value="initialScreeningCount + ' 条'" />
        <van-cell title="研判中" :value="judgingCount + ' 条'" />
        <van-cell title="验证中" :value="verifyingCount + ' 条'" />
        <van-cell title="已立项" :value="approvedCount + ' 条'" />
        <van-cell title="已上市" :value="launchedCount + ' 条'" />
      </van-cell-group>

      <van-cell-group title="设置" style="margin-top: 12px">
        <van-cell title="关于" value="线索提报平台 v1.0" />
        <van-cell title="清理缓存" is-link @click="clearCache" />
      </van-cell-group>
    </div>

    <!-- 底部导航栏 -->
    <van-tabbar v-model="activeTab" fixed placeholder safe-area-inset-bottom>
      <van-tabbar-item name="clues" icon="notes-o">线索提报</van-tabbar-item>
      <van-tabbar-item name="mine" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMyClues, deleteClue, withdrawClue } from '../api'
import { showToast, showSuccessToast, showFailToast, showConfirmDialog } from 'vant'
import { userStore } from '../stores/user.js'

const route = useRoute()
const router = useRouter()
const activeTab = ref('clues')
const clues = ref([])
const refreshing = ref(false)

const initialScreeningCount = computed(() =>
  clues.value.filter(c => c.status === 'initial_screening').length
)
const judgingCount = computed(() =>
  clues.value.filter(c => c.status === 'judging').length
)
const verifyingCount = computed(() =>
  clues.value.filter(c => c.status === 'verifying').length
)
const approvedCount = computed(() =>
  clues.value.filter(c => c.status === 'ipd_review').length
)
const launchedCount = computed(() =>
  clues.value.filter(c => c.status === 'launched').length
)

const statusMap = {
  new: '新建',
  pending_supplement: '待补充',
  initial_screening: '初筛中', judging: '研判中', verifying: '验证中',
  ipd_review: 'IPD立项', launched: '已上市',
  initial_screening_rejected: '初筛不通过', judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过', ipd_review_rejected: 'IPD不通过'
}

function statusLabel(s) {
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)' }
  return statusMap[s] || oldMap[s] || s
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN')
}

async function loadClues() {
  try {
    const { data } = await getMyClues()
    if (data.code === 0) clues.value = data.data || []
  } catch (e) { /* ignore */ }
}

function onRefresh() {
  loadClues().then(() => { refreshing.value = false })
}

function goToClue(clue) {
  if (clue.status === 'new') {
    router.push(`/submit?edit=${clue.id}`)
  } else if (clue.status === 'pending_supplement') {
    router.push(`/clue/${clue.id}`)
  } else {
    router.push(`/clue/${clue.id}`)
  }
}

function clearCache() {
  showToast('缓存已清理')
}

async function onDelete(clue) {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定删除线索「${clue.clueNo}」吗？删除后不可恢复。`,
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  clues.value = clues.value.filter(c => c.id !== clue.id)
  try {
    const { data } = await deleteClue(clue.id)
    if (data.code !== 0) throw new Error(data.message)
    showSuccessToast('删除成功')
  } catch {
    loadClues()
    showFailToast('删除失败')
  }
}

async function onWithdraw(clue) {
  try {
    await showConfirmDialog({
      title: '确认撤回',
      message: `确定撤回线索「${clue.clueNo}」吗？撤回后将变为新建状态。`,
      confirmButtonText: '撤回',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    const { data } = await withdrawClue(clue.id)
    if (data.code !== 0) throw new Error(data.message)
    showSuccessToast('已撤回')
    loadClues()
  } catch {
    showFailToast('撤回失败')
  }
}

onMounted(loadClues)

watch(() => route.path, (path) => {
  if (path === '/') loadClues()
})
</script>

<style scoped>
.user-home {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 110px;
}
.tab-content { padding-top: 46px; }
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
.clue-no { font-size: 12px; color: #999; }
.clue-title { font-size: 15px; font-weight: 500; margin-bottom: 8px; color: #333; }
.clue-meta { display: flex; justify-content: flex-end; font-size: 12px; color: #999; }
.clue-actions {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
  margin-top: 4px;
}
.action-btn {
  font-size: 12px;
  color: #1989fa;
  cursor: pointer;
}

.fab-wrapper {
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
  padding: 12px 16px;
  max-width: 750px;
  margin: 0 auto;
  background: linear-gradient(transparent, #f5f7fa 40%);
}

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
.profile-info .dept { font-size: 14px; color: #666; margin-bottom: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.profile-info .contact { font-size: 13px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
