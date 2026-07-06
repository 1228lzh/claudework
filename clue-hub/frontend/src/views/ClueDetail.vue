<template>
  <div class="clue-detail">
    <van-nav-bar title="线索详情" left-text="返回" left-arrow @click-left="router.back" fixed placeholder />

    <div v-if="clue" class="detail-content">
      <!-- 状态 -->
      <div class="status-bar">
        <span :class="['stage-tag', clue.status]">{{ statusLabel(clue.status) }}</span>
        <span class="clue-no">{{ clue.clueNo }}</span>
      </div>

      <!-- 平铺表单（可编辑/只读统一布局） -->
      <van-cell-group title="谁报的">
        <van-field v-model="form.reporterName" label="报备人" :readonly="!isEditable" :placeholder="isEditable ? '请输入姓名' : ''" />
        <van-field v-model="form.reporterDept" label="部门/单位" :readonly="!isEditable" :placeholder="isEditable ? '请输入部门/单位' : ''" />
        <van-field v-model="form.reporterContact" label="联系方式" :readonly="!isEditable" :placeholder="isEditable ? '请输入联系方式' : ''" />
      </van-cell-group>

      <van-cell-group title="什么线索">
        <van-field v-model="form.clueName" label="线索名称" :readonly="!isEditable" :placeholder="isEditable ? `例：'某代理商自己在做包管包柱，投入不大有利润'` : ''" maxlength="50" />
        <van-field v-model="form.clueType" label="线索类型" :is-link="isEditable" :readonly="!isEditable" @click="isEditable ? showClueTypePicker = true : null" :placeholder="isEditable ? '请选择线索类型' : ''" />
        <van-field v-if="form.clueType === '其他'" v-model="form.clueTypeOther" label="其他类型" :readonly="!isEditable" :placeholder="isEditable ? '请说明' : ''" />
        <div class="field-label">线索描述</div>
        <div v-if="isEditable" class="desc-hint">说清楚"发现了什么"+"为什么觉得这是个机会"即可</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.clueDesc" class="desc-textarea" rows="5"
            :placeholder="isEditable ? '请输入' : ''" maxlength="3000" :readonly="!isEditable" />
          <div class="desc-count">{{ (form.clueDesc || '').length }} / 3000</div>
        </div>
      </van-cell-group>

      <van-cell-group title="线索来源">
        <div class="field-label">信息来源（可多选）</div>
        <van-checkbox-group v-if="isEditable" v-model="form.infoSourceArr" class="cb-vertical">
          <van-checkbox v-for="item in infoSourceOptions" :key="item" :name="item" shape="square">{{ item }}</van-checkbox>
        </van-checkbox-group>
        <div v-else class="cb-group">
          <span v-for="item in infoSourceOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.infoSourceArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.infoSourceArr.includes('其他')" v-model="form.infoSourceOther" label="其他来源" :readonly="!isEditable" :placeholder="isEditable ? '请说明' : ''" />
        <van-field v-model="form.reliability" label="信息可靠度" :is-link="isEditable" :readonly="!isEditable" @click="isEditable ? showReliabilityPicker = true : null" :placeholder="isEditable ? '请选择' : ''" />
        <div class="field-label">预计市场规模</div>
        <div v-if="isEditable" class="desc-hint">请描述预计市场规模、增长趋势及相关数据</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.marketSize" class="desc-textarea" rows="4"
            :placeholder="isEditable ? '请输入' : ''" maxlength="500" :readonly="!isEditable" />
          <div class="desc-count">{{ (form.marketSize || '').length }} / 500</div>
        </div>
      </van-cell-group>

      <van-cell-group title="线索判断">
        <div class="field-label">涉及品类/产品线（可多选）</div>
        <van-checkbox-group v-if="isEditable" v-model="form.productLinesArr" direction="horizontal" class="cb-group">
          <van-checkbox v-for="item in productLineOptions" :key="item" :name="item" shape="square">{{ item }}</van-checkbox>
        </van-checkbox-group>
        <div v-else class="cb-group">
          <span v-for="item in productLineOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.productLinesArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.productLinesArr.includes('全新品类（具体）') || form.productLinesArr.includes('其他')" v-model="form.productLinesDetail" label="具体品类" :readonly="!isEditable" :placeholder="isEditable ? '请说明具体品类' : ''" />
        <div class="field-label">目标客户群体（可多选）</div>
        <van-checkbox-group v-if="isEditable" v-model="form.targetCustomersArr" direction="horizontal" class="cb-group">
          <van-checkbox v-for="item in customerOptions" :key="item" :name="item" shape="square">{{ item }}</van-checkbox>
        </van-checkbox-group>
        <div v-else class="cb-group">
          <span v-for="item in customerOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.targetCustomersArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.targetCustomersArr.includes('其他')" v-model="form.targetCustomersOther" label="其他客户" :readonly="!isEditable" :placeholder="isEditable ? '请说明' : ''" />
        <van-field v-model="form.urgency" label="时间紧迫度" :is-link="isEditable" :readonly="!isEditable" @click="isEditable ? showUrgencyPicker = true : null" :placeholder="isEditable ? '请选择' : ''" />
        <van-field v-model="form.productStatus" label="竞品情况" :is-link="isEditable" :readonly="!isEditable" @click="isEditable ? showProductStatusPicker = true : null" :placeholder="isEditable ? '请选择' : ''" />
        <van-field v-if="form.productStatus === '竞品已经在做了'" v-model="form.productStatusDetail" label="哪些竞品" :readonly="!isEditable" :placeholder="isEditable ? '请说明具体竞品' : ''" />
        <van-field v-if="clue.ipdApprovedAt" label="上市时间" :model-value="formatDate(clue.ipdApprovedAt)" readonly />
      </van-cell-group>

      <!-- 补充材料类型 -->
      <van-cell-group title="补充材料">
        <div class="field-label">如有以下材料，请附上：</div>
        <van-checkbox-group v-if="isEditable" v-model="form.supplementMaterialTypesArr" direction="horizontal" class="cb-group">
          <van-checkbox v-for="item in supplementMaterialOptions" :key="item" :name="item" shape="square">{{ item }}</van-checkbox>
        </van-checkbox-group>
        <div v-else class="cb-group">
          <span v-for="item in supplementMaterialOptions" :key="item" :class="['cb-tag', { 'cb-tag--checked': form.supplementMaterialTypesArr.includes(item) }]">{{ item }}</span>
        </div>
        <van-field v-if="form.supplementMaterialTypesArr.includes('其他')" v-model="form.supplementMaterialOther" label="其他材料" :readonly="!isEditable" :placeholder="isEditable ? '请说明' : ''" />
      </van-cell-group>

      <!-- 补充信息（待补充状态下可编辑，有数据时只读展示） -->
      <van-cell-group v-if="isEditable" title="补充信息">
        <div class="field-label">补充信息 <span style="color: #ee0a24;">*</span></div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.supplementInfo" class="desc-textarea" rows="5"
            placeholder="请根据审核意见补充所需信息" maxlength="3000" />
          <div class="desc-count">{{ (form.supplementInfo || '').length }} / 3000</div>
        </div>
        <div class="field-label">补充附件 <span class="optional-hint">（选填）</span></div>
        <div class="upload-wrapper">
          <van-uploader
            v-model="supplementFiles"
            :after-read="onSupplementFileRead"
            :before-delete="onSupplementFileDelete"
            multiple
            :max-count="5"
            accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx"
          />
          <div class="upload-hint">支持图片、文档，单文件≤20MB</div>
        </div>
      </van-cell-group>

      <div v-if="!isEditable && hasSupplementInfo" style="padding: 16px; background: #fff; margin: 8px 0;">
        <div style="font-size: 14px; font-weight: 500; color: #323233; margin-bottom: 8px;">补充信息</div>
        <div style="background: #f7f8fa; border-radius: 8px; padding: 12px; font-size: 14px; line-height: 1.6; white-space: pre-wrap;">{{ form.supplementInfo }}</div>
      </div>

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
          <div v-if="reviewAttachments[record.id]" class="review-files">
            <div v-for="att in reviewAttachments[record.id]" :key="att.id" class="review-file-item" @click="downloadFile(att.id)">
              <van-icon name="description" /> {{ att.originalName }}
            </div>
          </div>
        </div>
      </van-cell-group>
    </div>

    <!-- 待补充底部按钮 -->
    <div v-if="isEditable" class="supplement-bar safe-bottom">
      <van-button @click="saveCurrentDraft" plain>暂存</van-button>
      <van-button type="primary" @click="onResubmit" :loading="submitting">提交线索</van-button>
    </div>

    <!-- 线索类型选择 -->
    <van-action-sheet v-model:show="showClueTypePicker" :actions="clueTypeActions"
      @select="onSelectClueType" cancel-text="取消" />
    <van-action-sheet v-model:show="showReliabilityPicker" :actions="reliabilityActions"
      @select="onSelectReliability" cancel-text="取消" />
    <van-action-sheet v-model:show="showUrgencyPicker" :actions="urgencyActions"
      @select="onSelectUrgency" cancel-text="取消" />
    <van-action-sheet v-model:show="showProductStatusPicker" :actions="productStatusActions"
      @select="onSelectProductStatus" cancel-text="取消" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getClueDetail, getReviewHistory, getClueAttachments, getFileUrl, saveClue, submitClue, uploadClueFile, getReviewAttachments } from '../api'
import { showConfirmDialog } from 'vant'
import { userStore } from '../stores/user.js'

const router = useRouter()
const route = useRoute()
const clue = ref(null)
const reviewHistory = ref([])
const reviewAttachments = ref({})
const attachments = ref([])
const supplementFiles = ref([])
const submitting = ref(false)

const isEditable = computed(() => clue.value?.status === 'pending_supplement')
const hasSupplementInfo = computed(() => !!form.supplementInfo)

const form = reactive({
  reporterName: '', reporterDept: '', reporterContact: '',
  clueName: '', clueType: '', clueTypeOther: '', clueDesc: '',
  infoSourceArr: [], infoSourceOther: '', reliability: '', marketSize: '',
  productLinesArr: [], productLinesDetail: '', targetCustomersArr: [], targetCustomersOther: '',
  urgency: '', productStatus: '', productStatusDetail: '',
  supplementMaterialTypesArr: [],
  supplementMaterialOther: '',
  supplementInfo: ''
})

const clueTypeOptions = ['新产品/新规格需求', '新应用场景/新领域机会', '竞品新动作/竞品威胁', '政策/标准/法规变化带来的机会', '技术/材料/工艺突破带来的机会', '其他']
const infoSourceOptions = ['客户/业主直接反馈', '经销商/代理商反馈', '设计院/工程公司反馈', '安装公司/施工方反馈', '销售同事反馈', '行业展会/论坛/会议', '竞品观察/竞品情报', '行业媒体/行业报告', '政策/标准/法规文件', '内部市场侦察小组', '其他']
const reliabilityOptions = ['高——多个独立来源交叉验证', '中——单一可靠来源（如重点客户直接反馈）', '低——道听途说/单次偶然获取']
const productLineOptions = ['给水领域', '排水领域', '暖通领域', '燃气领域', '市政领域', '全新品类（具体）', '其他']
const customerOptions = ['房地产开发商', '市政工程', '设计院', '安装公司', '经销商', '家装用户', '工业用户', '其他']
const urgencyOptions = ['紧急——窗口期很短（3个月内需要响应）', '较急——半年内应该行动', '不急——可以从容评估（1年以上窗口期）', '不确定']
const productStatusOptions = ['竞品已经在做了', '竞品还没做，但可能在关注', '市场上还没有人做', '不清楚']
const supplementMaterialOptions = ['客户需求原始记录/邮件/聊天截图', '竞品产品照片/资料', '行业报告/政策文件', '相关技术资料', '其他']

const showClueTypePicker = ref(false)
const showReliabilityPicker = ref(false)
const showUrgencyPicker = ref(false)
const showProductStatusPicker = ref(false)

const clueTypeActions = clueTypeOptions.map(v => ({ name: v }))
const reliabilityActions = reliabilityOptions.map(v => ({ name: v }))
const urgencyActions = urgencyOptions.map(v => ({ name: v }))
const productStatusActions = productStatusOptions.map(v => ({ name: v }))

const onSelectClueType = ({ name }) => { form.clueType = name; showClueTypePicker.value = false }
const onSelectReliability = ({ name }) => { form.reliability = name; showReliabilityPicker.value = false }
const onSelectUrgency = ({ name }) => { form.urgency = name; showUrgencyPicker.value = false }
const onSelectProductStatus = ({ name }) => { form.productStatus = name; showProductStatusPicker.value = false }

const statusMap = {
  new: '新建', pending_supplement: '待补充',
  initial_screening: '初筛中', judging: '研判中', verifying: '验证中',
  ipd_review: 'IPD立项', launched: '已上市',
  initial_screening_rejected: '初筛不通过', judging_rejected: '研判不通过',
  verifying_rejected: '验证不通过', ipd_review_rejected: 'IPD不通过'
}
const stageLabels = { initial_screening: '初筛', judging: '研判', verifying: '验证', ipd_review: 'IPD立项', launched: '已上市' }
const actionLabels = { pass: '通过', reject: '不通过', return: '退回补充', withdraw: '撤回' }

function statusLabel(s) {
  const oldMap = { submitted: '待初筛(旧)', returned: '退回(旧)', rejected: '不通过(旧)', approved: '已通过(旧)', draft: '草稿(旧)' }
  return statusMap[s] || oldMap[s] || s
}
function stageLabel(s) { return stageLabels[s] || s }
function actionLabel(a) { return actionLabels[a] || a }
function formatDate(d) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }

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
  if (clueData.supplementMaterialTypes) form.supplementMaterialTypesArr = clueData.supplementMaterialTypes.split(',')
  form.supplementMaterialOther = clueData.supplementMaterialOther || ''
  form.supplementInfo = clueData.supplementInfo || ''
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
    if (historyRes.data.code === 0) {
      reviewHistory.value = historyRes.data.data || []
      const raMap = {}
      await Promise.all(reviewHistory.value.map(async (r) => {
        try {
          const { data: attData } = await getReviewAttachments(r.id)
          if (attData.code === 0 && attData.data.length) raMap[r.id] = attData.data
        } catch (e) { /* ignore */ }
      }))
      reviewAttachments.value = raMap
    }
    if (attRes.data.code === 0) attachments.value = attRes.data.data || []
  } catch (e) { console.error('加载失败', e) }
})

function buildPayload() {
  return {
    reporterName: form.reporterName,
    reporterDept: form.reporterDept,
    reporterContact: form.reporterContact,
    wecomUserId: clue.value.wecomUserId || userStore.userId,
    clueName: form.clueName,
    clueType: form.clueType,
    clueTypeOther: form.clueTypeOther,
    clueDesc: form.clueDesc,
    infoSource: form.infoSourceArr.join(','),
    infoSourceOther: form.infoSourceOther,
    reliability: form.reliability,
    marketSize: form.marketSize,
    productLines: form.productLinesArr.join(','),
    productLinesDetail: form.productLinesDetail,
    targetCustomers: form.targetCustomersArr.join(','),
    targetCustomersOther: form.targetCustomersOther,
    urgency: form.urgency,
    productStatus: form.productStatus,
    productStatusDetail: form.productStatusDetail,
    supplementMaterialTypes: form.supplementMaterialTypesArr.join(','),
    supplementMaterialOther: form.supplementMaterialOther,
    supplementInfo: form.supplementInfo,
    draftId: clue.value.id,
    action: 'save'
  }
}

async function saveCurrentDraft() {
  try {
    const payload = buildPayload()
    const { data } = await saveClue(payload)
    if (data.code === 0) {
      showMyToast('暂存成功')
    } else {
      showMyToast(data.message || '暂存失败')
    }
  } catch (e) {
    showMyToast('暂存失败，请重试')
  }
}

async function onResubmit() {
  // 待补充状态下，补充信息必填
  if (!form.supplementInfo || !form.supplementInfo.trim()) {
    showMyToast('请填写补充信息')
    return
  }
  submitting.value = true
  try {
    const payload = buildPayload()
    payload.action = 'submit'
    const { data } = await submitClue(payload)
    if (data.code === 0) {
      showMyToast('提交成功')
      router.push('/')
    } else {
      showMyToast(data.message || '提交失败')
    }
  } catch (e) {
    showMyToast('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

function downloadFile(attachmentId) { window.open(getFileUrl(attachmentId), '_blank') }

async function onSupplementFileRead(file) {
  if (!clue.value || !clue.value.id) return
  try {
    const { data } = await uploadClueFile(clue.value.id, file.file)
    if (data.code === 0) {
      showMyToast('附件上传成功')
      // 刷新附件列表
      const attRes = await getClueAttachments(clue.value.id)
      if (attRes.data.code === 0) attachments.value = attRes.data.data || []
    } else {
      showMyToast(data.message || '上传失败')
      // 移除上传失败的文件
      supplementFiles.value = supplementFiles.value.filter(f => f !== file)
    }
  } catch (e) {
    showMyToast('上传失败，请重试')
    supplementFiles.value = supplementFiles.value.filter(f => f !== file)
  }
}

function onSupplementFileDelete(file) {
  return true
}

function formatSize(bytes) {
  if (!bytes) return '0 B'
  const u = ['B', 'KB', 'MB', 'GB']
  let i = 0, s = bytes
  while (s >= 1024 && i < u.length - 1) { s /= 1024; i++ }
  return s.toFixed(1) + ' ' + u[i]
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

.field-label {
  padding: 12px 16px 8px;
  font-size: 14px;
  color: #323233;
  font-weight: 600;
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
}
.desc-textarea::placeholder { color: #c8c9cc; }
.desc-textarea[readonly] { background: #f7f8fa; color: #666; resize: none; }
.desc-count {
  text-align: right;
  padding: 4px 12px 8px;
  font-size: 12px;
  color: #999;
  background: #fff;
}
.desc-hint {
  padding: 0 16px 8px;
  font-size: 12px;
  color: #999;
}
.cb-group {
  padding: 0 16px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.cb-vertical { display: block; }
.cb-vertical :deep(.van-checkbox) { margin-bottom: 8px; }
.cb-group :deep(.van-checkbox) { margin: 0; }

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
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 13px;
}
.review-stage { font-weight: 500; }
.review-action.pass { color: #52c41a; }
.review-action.reject { color: #f5222d; }
.review-action.return { color: #faad14; }
.review-action.withdraw { color: #faad14; }
.review-time { font-size: 12px; color: #999; margin-left: auto; }
.review-comment { margin-top: 8px; font-size: 13px; color: #666; background: #f7f8fa; padding: 8px; border-radius: 4px; }
.review-files { margin-top: 8px; }
.review-file-item {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 13px; color: #1989fa; cursor: pointer;
  padding: 4px 10px; background: #f0f7ff; border-radius: 4px;
  margin-right: 8px; margin-bottom: 4px;
}

.supplement-bar {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #ebedf0;
  position: fixed;
  bottom: 0; left: 0; right: 0;
  max-width: 750px;
  margin: 0 auto;
}
.supplement-bar .van-button { flex: 1; }

.upload-wrapper {
  padding: 0 16px 12px;
}
.upload-hint {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
.optional-hint {
  font-size: 12px;
  color: #999;
  font-weight: 400;
}

:deep(.van-field__label) {
  font-weight: 600;
}
</style>
