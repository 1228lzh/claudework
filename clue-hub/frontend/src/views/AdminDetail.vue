<template>
  <div class="admin-detail">
    <van-nav-bar title="审核详情" left-text="返回" left-arrow @click-left="router.back" fixed placeholder />

    <div v-if="clue" class="detail-content">
      <!-- 平铺表单（只读） -->
      <van-cell-group title="谁报的">
        <van-field v-model="form.reporterName" label="报备人" readonly />
        <van-field v-model="form.reporterDept" label="部门/单位" readonly />
        <van-field v-model="form.reporterContact" label="联系方式" readonly />
      </van-cell-group>

      <van-cell-group title="什么线索">
        <van-field v-model="form.clueName" label="线索名称" readonly />
        <van-field v-model="form.clueType" label="线索类型" readonly />
        <van-field v-if="form.clueTypeOther" v-model="form.clueTypeOther" label="其他类型" readonly />
        <div class="field-label">线索描述</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.clueDesc" class="desc-textarea" rows="5" readonly />
          <div class="desc-count">{{ (form.clueDesc || '').length }} / 500</div>
        </div>
      </van-cell-group>

      <van-cell-group title="线索来源">
        <div class="field-label">信息来源（可多选）</div>
        <div class="cb-group">
          <span v-for="item in infoSourceOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.infoSourceArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.infoSourceArr.includes('其他')" v-model="form.infoSourceOther" label="其他来源" readonly />
        <van-field v-model="form.reliability" label="信息可靠度" readonly />
        <div class="field-label">预计市场规模</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.marketSize" class="desc-textarea" rows="4" readonly />
          <div class="desc-count">{{ (form.marketSize || '').length }} / 500</div>
        </div>
      </van-cell-group>

      <van-cell-group title="线索判断">
        <div class="field-label">涉及品类/产品线（可多选）</div>
        <div class="cb-group">
          <span v-for="item in productLineOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.productLinesArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.productLinesArr.includes('全品类')" v-model="form.productLinesDetail" label="全品类说明" readonly />
        <div class="field-label">目标客户群体（可多选）</div>
        <div class="cb-group">
          <span v-for="item in customerOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.targetCustomersArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.targetCustomersArr.includes('其他')" v-model="form.targetCustomersOther" label="其他客户" readonly />
        <van-field v-model="form.urgency" label="时间紧迫度" readonly />
        <van-field v-model="form.productStatus" label="竞品情况" readonly />
        <van-field v-if="form.productStatusDetail" v-model="form.productStatusDetail" label="哪家" readonly />
      </van-cell-group>

      <!-- 附件 -->
      <van-cell-group v-if="attachments.length" title="附件">
        <van-cell v-for="att in attachments" :key="att.id" :title="att.originalName"
          :label="formatSize(att.fileSize)" is-link @click="downloadFile(att.id)" />
      </van-cell-group>

      <!-- 立项信息 -->
      <van-cell-group v-if="clue.status === 'ipd_review' && (clue.ipdApprovedAt || clue.completedAt)" title="立项信息">
        <van-field v-if="clue.ipdApprovedAt" label="IPD立项时间" :model-value="formatDate(clue.ipdApprovedAt)" readonly />
        <van-field v-if="clue.completedAt" label="完结时间" :model-value="formatDate(clue.completedAt)" readonly />
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
        <van-field v-model="reviewComment" type="textarea" rows="3" placeholder="审核意见（退回/不通过时必填）"
          show-word-limit maxlength="500" />

        <!-- 审核附件上传 -->
        <div class="review-upload">
          <van-uploader :after-read="onReviewFileRead" multiple :max-count="5"
            accept="*" upload-icon="plus"
            @delete="onReviewFileDelete" />
        </div>

        <!-- 验证通过时填写 IPD 立项时间和完结时间 -->
        <template v-if="clue.status === 'verifying'">
          <div class="native-date-field">
            <span class="native-date-label">IPD立项时间</span>
            <input v-model="ipdApprovedDate" type="date" class="native-date-input" />
          </div>
          <div class="native-date-field">
            <span class="native-date-label">完结时间</span>
            <input v-model="completedDate" type="date" class="native-date-input" />
          </div>
        </template>

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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getClueDetail, getReviewHistory, getClueAttachments, submitReview, uploadReviewFile, getFileUrl } from '../api'
import { userStore } from '../stores/user.js'

let toastTimer = null
function showMyToast(msg) {
  const old = document.querySelector('.my-toast')
  if (old) { clearTimeout(toastTimer); old.remove() }
  const el = document.createElement('div')
  el.className = 'my-toast'
  el.textContent = msg
  el.style.cssText = 'position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);background:rgba(0,0,0,.7);color:#fff;padding:12px 24px;border-radius:8px;z-index:9999;font-size:14px;white-space:nowrap;'
  document.body.appendChild(el)
  toastTimer = setTimeout(() => { const e = document.querySelector('.my-toast'); if (e) e.remove() }, 1500)
}

const router = useRouter()
const route = useRoute()
const clue = ref(null)
const reviewHistory = ref([])
const attachments = ref([])
const reviewComment = ref('')
const ipdApprovedDate = ref('')
const completedDate = ref('')
const pendingReviewFiles = ref([])

const form = reactive({
  reporterName: '', reporterDept: '', reporterContact: '',
  clueName: '', clueType: '', clueTypeOther: '', clueDesc: '',
  infoSourceArr: [], infoSourceOther: '', reliability: '', marketSize: '',
  productLinesArr: [], productLinesDetail: '', targetCustomersArr: [], targetCustomersOther: '',
  urgency: '', productStatus: '', productStatusDetail: ''
})

const infoSourceOptions = ['客户/业务直接反馈', '经销商/代理商反馈', '设计院/工程公司反馈', '装修公司/施工方反馈', '行业同事反馈', '行业展会/论坛/交流', '产品观察/产品情报', '行业媒体/行业报告', '政策/标准/法规文件', '其他']
const productLineOptions = ['给水管道', '排水管道', '暖通管道', '燃气管道', '新风管道', '净水/水处理', '全品类']
const customerOptions = ['房地产开发商', '施工单位', '设计院', '装修公司', '经销商', '最终用户', '工业用户', '其他']

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
// IPD立项时间变更时，自动设置完结时间为三年后
watch(ipdApprovedDate, (val) => {
  if (val) {
    const d = new Date(val)
    d.setFullYear(d.getFullYear() + 3)
    completedDate.value = d.toISOString().split('T')[0]
  }
})

const canReview = computed(() => clue.value && reviewableStatuses.includes(clue.value.status))

function loadClueIntoForm(clueData) {
  form.reporterName = clueData.reporterName || ''
  form.reporterDept = clueData.reporterDept || ''
  form.reporterContact = clueData.reporterContact || ''
  form.clueName = clueData.clueName || ''
  form.clueType = clueData.clueType || ''
  form.clueTypeOther = clueData.clueTypeOther || ''
  form.clueDesc = clueData.clueDesc || ''
  if (clueData.infoSource) form.infoSourceArr = clueData.infoSource.split(',')
  form.infoSourceOther = clueData.infoSourceOther || ''
  form.reliability = clueData.reliability || ''
  form.marketSize = clueData.marketSize || ''
  if (clueData.productLines) form.productLinesArr = clueData.productLines.split(',')
  form.productLinesDetail = clueData.productLinesDetail || ''
  if (clueData.targetCustomers) form.targetCustomersArr = clueData.targetCustomers.split(',')
  form.targetCustomersOther = clueData.targetCustomersOther || ''
  form.urgency = clueData.urgency || ''
  form.productStatus = clueData.productStatus || ''
  form.productStatusDetail = clueData.productStatusDetail || ''
}

onMounted(async () => {
  const id = route.params.id
  try {
    const [clueRes, historyRes, attRes] = await Promise.all([
      getClueDetail(id), getReviewHistory(id), getClueAttachments(id)
    ])
    if (clueRes.data.code === 0) {
      clue.value = clueRes.data.data
      loadClueIntoForm(clue.value)
    }
    if (historyRes.data.code === 0) reviewHistory.value = historyRes.data.data || []
    if (attRes.data.code === 0) attachments.value = attRes.data.data || []
  } catch (e) { console.error('加载失败', e) }
})

async function doReview(action) {
  const comment = reviewComment.value.trim()
  if ((action === 'reject' || action === 'return') && !comment) {
    showMyToast('请填写审核意见')
    return
  }
  if (action === 'pass' && clue.value.status === 'verifying') {
    if (!ipdApprovedDate.value) { showMyToast('请选择IPD立项时间'); return }
    if (!completedDate.value) { showMyToast('请选择完结时间'); return }
  }
  try {
    const { data } = await submitReview(clue.value.id, {
      action,
      comment: comment || '通过',
      reviewerName: userStore.fullname || '审核员',
      ipdApprovedAt: ipdApprovedDate.value ? ipdApprovedDate.value + 'T00:00:00' : null,
      completedAt: completedDate.value ? completedDate.value + 'T00:00:00' : null
    })
    if (data.code === 0) {
      // 上传审核附件
      if (pendingReviewFiles.value.length > 0 && data.data.id) {
        for (const f of pendingReviewFiles.value) {
          await uploadReviewFile(data.data.id, f.file)
        }
      }
      showMyToast('操作成功')
      setTimeout(() => router.back(), 800)
    } else {
      showMyToast(data.message || '操作失败')
    }
  } catch (e) {
    showMyToast('操作失败')
  }
}

function onReviewFileRead(files) {
  const items = Array.isArray(files) ? files : [files]
  pendingReviewFiles.value.push(...items)
}
function onReviewFileDelete(file) {
  pendingReviewFiles.value = pendingReviewFiles.value.filter(f => f !== file)
}

function downloadFile(attachmentId) {
  window.open(getFileUrl(attachmentId), '_blank')
}
</script>

<style scoped>
.admin-detail { padding-bottom: 380px; background: #f5f7fa; min-height: 100vh; box-sizing: border-box; }

@media (min-width: 768px) {
  .admin-detail { padding-bottom: 0; }
}
.detail-content { padding-top: 8px; }

.field-label {
  padding: 12px 16px 8px;
  font-size: 14px;
  color: #323233;
  font-weight: 500;
}
.desc-textarea-wrapper {
  margin: 0 16px;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  overflow: hidden;
}
.desc-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: none;
  font-size: 14px;
  line-height: 1.6;
  color: #323233;
  resize: vertical;
  box-sizing: border-box;
  font-family: inherit;
  outline: none;
  background: #f7f8fa;
  color: #666;
  resize: none;
}
.desc-count {
  text-align: right;
  padding: 4px 12px 8px;
  font-size: 12px;
  color: #999;
  background: #fff;
}
.cb-group {
  padding: 0 16px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cb-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  background: #f5f5f5;
  color: #c8c9cc;
}
.cb-tag::before {
  content: '';
  width: 14px;
  height: 14px;
  border: 1px solid #c8c9cc;
  border-radius: 2px;
  flex-shrink: 0;
}
.cb-tag--checked {
  background: #e8f4ff;
  color: #323233;
  font-weight: 500;
}
.cb-tag--checked::before {
  content: '✓';
  display: flex;
  align-items: center;
  justify-content: center;
  border-color: #1989fa;
  background: #1989fa;
  color: #fff;
  font-size: 10px;
  line-height: 1;
}

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
  max-width: 1200px;
  margin: 0 auto;
}

@media (min-width: 768px) {
  .review-actions {
    position: static;
    max-width: none;
  }
}
.review-upload { padding: 8px 0; }
.native-date-field {
  display: flex; align-items: center;
  padding: 10px 16px; background: #fff;
  border-bottom: 1px solid #ebedf0;
}
.native-date-label { width: 90px; font-size: 14px; color: #323233; flex-shrink: 0; }
.native-date-input {
  flex: 1; border: none; font-size: 14px; color: #323233;
  background: transparent; outline: none; -webkit-appearance: none;
}
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
  max-width: 1200px;
  margin: 0 auto;
}

@media (min-width: 768px) {
  .final-status {
    position: static;
    max-width: none;
  }
}
</style>
