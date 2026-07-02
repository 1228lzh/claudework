<template>
  <div class="clue-detail">
    <van-nav-bar title="线索详情" left-text="返回" left-arrow @click-left="router.back" fixed placeholder />

    <div v-if="clue" class="detail-content">
      <!-- 状态 -->
      <div class="status-bar">
        <span :class="['stage-tag', clue.status]">{{ statusLabel(clue.status) }}</span>
        <span class="clue-no">{{ clue.clueNo }}</span>
      </div>

      <!-- 一审：谁报的 -->
      <van-cell-group title="谁报的">
        <van-cell title="报备人" :value="clue.reporterName" />
        <van-cell title="部门/单位" :value="clue.reporterDept || '-'" />
        <van-cell title="联系方式" :value="clue.reporterContact || '-'" />
      </van-cell-group>

      <!-- 二审：什么线索 -->
      <van-cell-group title="什么线索">
        <van-cell title="线索名称" :value="clue.clueName" />
        <van-cell title="线索类型" :value="clue.clueType" />
        <van-cell title="线索描述" :label="clue.clueDesc" />
      </van-cell-group>

      <!-- 三审：线索来源 -->
      <van-cell-group title="线索来源">
        <van-cell title="信息来源" :value="clue.infoSource || '-'" />
        <van-cell title="可靠度" :value="clue.reliability || '-'" />
        <van-cell title="预计市场规模" :value="clue.marketSize || '-'" />
      </van-cell-group>

      <!-- 四审：线索判断 -->
      <van-cell-group title="线索判断">
        <van-cell title="涉及品类" :value="clue.productLines || '-'" />
        <van-cell title="目标客户" :value="clue.targetCustomers || '-'" />
        <van-cell title="时间紧迫度" :value="clue.urgency || '-'" />
        <van-cell title="竞品情况" :value="clue.productStatus || '-'" />
      </van-cell-group>

      <!-- 审核历史 -->
      <van-cell-group v-if="reviewHistory.length" title="审核记录">
        <div v-for="record in reviewHistory" :key="record.id" class="review-record">
          <div class="review-header">
            <span class="review-stage">{{ stageLabel(record.reviewStage) }}</span>
            <span :class="['review-action', record.action]">{{ actionLabel(record.action) }}</span>
            <span class="review-time">{{ formatDate(record.reviewedAt) }}</span>
          </div>
          <div v-if="record.comment" class="review-comment">{{ record.comment }}</div>
        </div>
      </van-cell-group>

      <!-- 退回补充后重新编辑 -->
      <div v-if="clue.status === 'returned'" class="resubmit-bar safe-bottom">
        <van-button type="primary" block @click="onResubmit">重新编辑提交</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getClueDetail, getReviewHistory } from '../api'

const router = useRouter()
const route = useRoute()
const clue = ref(null)
const reviewHistory = ref([])

const statusMap = {
  new: '新建',
  initial_screening: '初筛中', judging: '研判中', verifying: '验证中',
  ipd_review: 'IPD立项',
  initial_screening_rejected: '初筛不通过', judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过'
}
const stageLabels = {
  initial_screening: '初筛', judging: '研判', verifying: '验证',
  ipd_review: 'IPD立项'
}
const actionLabels = { pass: '通过', reject: '不通过', return: '退回补充' }

function statusLabel(s) {
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)', draft: '草稿(旧)' }
  return statusMap[s] || oldMap[s] || s
}
function stageLabel(s) { return stageLabels[s] || s }
function actionLabel(a) { return actionLabels[a] || a }
function formatDate(d) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }

onMounted(async () => {
  const id = route.params.id
  try {
    const [clueRes, historyRes] = await Promise.all([
      getClueDetail(id),
      getReviewHistory(id)
    ])
    if (clueRes.data.code === 0) clue.value = clueRes.data.data
    if (historyRes.data.code === 0) reviewHistory.value = historyRes.data.data || []
  } catch (e) {
    console.error('加载失败', e)
  }
})

function onResubmit() {
  router.push({ path: '/submit', query: { resubmit: clue.value?.id } })
}
</script>

<style scoped>
.clue-detail {
  padding-bottom: 80px;
  background: #f5f7fa;
  min-height: 100vh;
}
.detail-content {
  padding-top: 8px;
}
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  margin-bottom: 8px;
}
.clue-no { font-size: 12px; color: #999; }
.review-record {
  padding: 12px 16px;
  border-bottom: 1px solid #ebedf0;
}
.review-header {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 13px;
}
.review-stage { font-weight: 500; }
.review-action.pass { color: #52c41a; }
.review-action.reject { color: #f5222d; }
.review-action.return { color: #faad14; }
.review-time { font-size: 12px; color: #999; margin-left: auto; }
.review-comment { margin-top: 8px; font-size: 13px; color: #666; background: #f7f8fa; padding: 8px; border-radius: 4px; }
.resubmit-bar { padding: 16px; background: #fff; border-top: 1px solid #ebedf0; position: fixed; bottom: 0; left: 0; right: 0; }
</style>
