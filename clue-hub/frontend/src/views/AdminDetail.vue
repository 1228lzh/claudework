<template>
  <div class="admin-detail">
    <van-nav-bar title="审核详情" left-text="返回" left-arrow @click-left="router.back" fixed placeholder />

    <div v-if="clue" class="detail-content">
      <!-- 线索信息 -->
      <van-cell-group title="谁报的">
        <van-cell title="报备人" :value="clue.reporterName" />
        <van-cell title="部门/单位" :value="clue.reporterDept || '-'" />
        <van-cell title="联系方式" :value="clue.reporterContact || '-'" />
      </van-cell-group>
      <van-cell-group title="什么线索">
        <van-cell title="线索名称" :value="clue.clueName" />
        <van-cell title="线索类型" :value="clue.clueType" />
        <van-cell v-if="clue.clueTypeOther" title="其他类型" :value="clue.clueTypeOther" />
        <van-cell title="线索描述" :label="clue.clueDesc" />
      </van-cell-group>
      <van-cell-group title="线索来源">
        <van-cell title="信息来源" :value="clue.infoSource || '-'" />
        <van-cell title="可靠度" :value="clue.reliability || '-'" />
        <van-cell title="预计市场规模" :value="clue.marketSize || '-'" />
      </van-cell-group>
      <van-cell-group title="线索判断">
        <van-cell title="涉及品类" :value="clue.productLines || '-'" />
        <van-cell title="目标客户" :value="clue.targetCustomers || '-'" />
        <van-cell title="时间紧迫度" :value="clue.urgency || '-'" />
        <van-cell title="竞品情况" :value="clue.productStatus || '-'" />
      </van-cell-group>

      <!-- 附件 -->
      <van-cell-group v-if="attachments.length" title="附件">
        <van-cell v-for="att in attachments" :key="att.id" :title="att.originalName"
          :label="formatSize(att.fileSize)" is-link @click="downloadFile(att.id)" />
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

      <!-- 审核操作区 -->
      <div v-if="canReview" class="review-actions safe-bottom">
        <van-field v-model="reviewComment" type="textarea" rows="3" placeholder="审核意见（选填）"
          show-word-limit maxlength="500" />

        <!-- 审核附件上传 -->
        <div class="review-upload">
          <van-uploader :after-read="onReviewFileRead" multiple :max-count="5"
            @delete="onReviewFileDelete" />
        </div>

        <div class="action-buttons">
          <template v-if="clue.status === 'initial_screening'">
            <van-button type="danger" plain @click="doReview('reject')">不通过</van-button>
            <van-button type="warning" plain @click="doReview('return')">退回补充</van-button>
            <van-button type="primary" @click="doReview('pass')">通过</van-button>
          </template>
          <template v-else>
            <van-button type="danger" plain @click="doReview('reject')">不通过</van-button>
            <van-button type="primary" @click="doReview('pass')">通过</van-button>
          </template>
        </div>
      </div>

      <!-- 已终审提示 -->
      <div v-else class="final-status safe-bottom">
        <span>此线索已{{ clue.status === 'ipd_review' ? 'IPD立项' : '结束' }}，无需审核操作</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getClueDetail, getReviewHistory, getClueAttachments, submitReview, uploadReviewFile, getFileUrl } from '../api'
import { showToast, showSuccessToast, showFailToast } from 'vant'

const router = useRouter()
const route = useRoute()
const clue = ref(null)
const reviewHistory = ref([])
const attachments = ref([])
const reviewComment = ref('')
const pendingReviewFiles = ref([])

const stageLabels = {
  initial_screening: '初筛', judging: '研判', verifying: '验证', ipd_review: 'IPD立项'
}
const actionLabels = { pass: '通过', reject: '不通过', return: '退回补充' }

function stageLabel(s) { return stageLabels[s] || s }
function actionLabel(a) { return actionLabels[a] || a }
function formatDate(d) { return d ? new Date(d).toLocaleString('zh-CN') : '' }
function formatSize(bytes) {
  if (!bytes) return '0 B'
  const u = ['B', 'KB', 'MB', 'GB']
  let i = 0, s = bytes
  while (s >= 1024 && i < u.length - 1) { s /= 1024; i++ }
  return s.toFixed(1) + ' ' + u[i]
}

const reviewableStatuses = ['initial_screening', 'judging', 'verifying']
const canReview = computed(() => clue.value && reviewableStatuses.includes(clue.value.status))

onMounted(async () => {
  const id = route.params.id
  try {
    const [clueRes, historyRes, attRes] = await Promise.all([
      getClueDetail(id), getReviewHistory(id), getClueAttachments(id)
    ])
    if (clueRes.data.code === 0) clue.value = clueRes.data.data
    if (historyRes.data.code === 0) reviewHistory.value = historyRes.data.data || []
    if (attRes.data.code === 0) attachments.value = attRes.data.data || []
  } catch (e) { console.error('加载失败', e) }
})

async function doReview(action) {
  try {
    const { data } = await submitReview(clue.value.id, {
      action,
      comment: reviewComment.value,
      reviewerName: '审核员'
    })
    if (data.code === 0) {
      // 上传审核附件
      if (pendingReviewFiles.value.length > 0 && data.data.id) {
        for (const f of pendingReviewFiles.value) {
          await uploadReviewFile(data.data.id, f.file)
        }
      }
      showSuccessToast(actionLabels[action] + '操作完成')
      router.back()
    } else {
      showFailToast(data.message || '操作失败')
    }
  } catch (e) {
    showFailToast('操作失败')
  }
}

function onReviewFileRead(file) { pendingReviewFiles.value.push(file) }
function onReviewFileDelete(file) {
  pendingReviewFiles.value = pendingReviewFiles.value.filter(f => f !== file)
}

function downloadFile(attachmentId) {
  window.open(getFileUrl(attachmentId), '_blank')
}
</script>

<style scoped>
.admin-detail { padding-bottom: 200px; background: #f5f7fa; min-height: 100vh; }
.detail-content { padding-top: 8px; }
.review-record {
  padding: 12px 16px;
  border-bottom: 1px solid #ebedf0;
}
.review-header {
  display: flex; gap: 8px; align-items: center; font-size: 13px;
}
.review-stage { font-weight: 500; }
.review-action.pass { color: #52c41a; }
.review-action.reject { color: #f5222d; }
.review-action.return { color: #faad14; }
.review-time { font-size: 12px; color: #999; margin-left: auto; }
.review-comment { margin-top: 8px; font-size: 13px; color: #666; background: #f7f8fa; padding: 8px; border-radius: 4px; }

.review-actions {
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #ebedf0;
  position: fixed;
  bottom: 0; left: 0; right: 0;
  max-width: 750px;
  margin: 0 auto;
}
.review-upload { padding: 8px 0; }
.action-buttons {
  display: flex; gap: 12px; margin-top: 12px;
}
.action-buttons .van-button { flex: 1; }
.final-status {
  padding: 20px 16px;
  text-align: center;
  color: #999;
  background: #fff;
  border-top: 1px solid #ebedf0;
  position: fixed;
  bottom: 0; left: 0; right: 0;
  max-width: 750px;
  margin: 0 auto;
}
</style>
