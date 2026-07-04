<template>
  <div class="submit-form">
    <!-- 顶部导航 -->
    <van-nav-bar title="线索提报" left-text="返回" left-arrow @click-left="onBack" fixed placeholder />

    <!-- 步骤条 -->
    <van-steps :active="currentStep" active-color="#1989fa">
      <van-step>谁报的</van-step>
      <van-step>什么线索</van-step>
      <van-step>线索来源</van-step>
      <van-step>线索判断</van-step>
      <van-step>补充材料</van-step>
    </van-steps>

    <!-- 表单内容 -->
    <div class="step-content">
      <!-- 第一步：谁报的 -->
      <template v-if="currentStep === 0">
        <van-field v-model="form.reporterName" label="报备人" placeholder="请输入姓名" required :rules="[{ required: true }]" />
        <van-field v-model="form.reporterDept" label="部门/单位" placeholder="请输入部门/单位" />
        <van-field v-model="form.reporterContact" label="联系方式" placeholder="请输入联系方式" />
        <div class="hint-text">💡 企微用户信息将自动带出，可手动修改</div>
      </template>

      <!-- 第二步：什么线索 -->
      <template v-if="currentStep === 1">
        <van-field v-model="form.clueName" label="线索名称" placeholder="一句话描述，不超过50字" maxlength="50" required
          :rules="[{ required: true, message: '请填写线索名称' }]" />
        <van-field v-model="form.clueType" label="线索类型" is-link readonly @click="showClueTypePicker = true" required
          placeholder="请选择线索类型" :rules="[{ required: true }]" />
        <van-field v-if="form.clueType === '其他'" v-model="form.clueTypeOther" label="其他类型" placeholder="请说明" />
        <div class="field-label required">线索描述</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.clueDesc" class="desc-textarea" rows="6"
            placeholder="3-5句话说明：是什么 + 为什么是机会" maxlength="500" />
          <div class="desc-count">{{ form.clueDesc.length }} / 500</div>
        </div>
      </template>

      <!-- 第三步：线索来源 -->
      <template v-if="currentStep === 2">
        <div class="field-label required">信息来源（可多选）</div>
        <van-checkbox-group v-model="form.infoSourceArr" direction="horizontal">
          <van-checkbox v-for="item in infoSourceOptions" :key="item" :name="item" shape="square">
            {{ item }}
          </van-checkbox>
        </van-checkbox-group>
        <van-field v-if="form.infoSourceArr.includes('其他')" v-model="form.infoSourceOther" label="其他来源" placeholder="请说明" />

        <van-field v-model="form.reliability" label="信息可靠度" is-link readonly required
          @click="showReliabilityPicker = true" placeholder="请选择" />
        <div class="field-label required">预计市场规模</div>
        <div class="desc-textarea-wrapper">
          <textarea v-model="form.marketSize" class="desc-textarea" rows="6"
            placeholder="请描述预计市场规模、增长趋势及相关数据" maxlength="500" />
          <div class="desc-count">{{ form.marketSize.length }} / 500</div>
        </div>
      </template>

      <!-- 第四步：线索判断 -->
      <template v-if="currentStep === 3">
        <div class="field-label">涉及品类/产品线（可多选）</div>
        <van-checkbox-group v-model="form.productLinesArr" direction="horizontal">
          <van-checkbox v-for="item in productLineOptions" :key="item" :name="item" shape="square">
            {{ item }}
          </van-checkbox>
        </van-checkbox-group>
        <van-field v-if="form.productLinesArr.includes('全品类')" v-model="form.productLinesDetail" label="全品类说明" placeholder="请说明具体品类" />

        <div class="field-label">目标客户群体（可多选）</div>
        <van-checkbox-group v-model="form.targetCustomersArr" direction="horizontal">
          <van-checkbox v-for="item in customerOptions" :key="item" :name="item" shape="square">
            {{ item }}
          </van-checkbox>
        </van-checkbox-group>
        <van-field v-if="form.targetCustomersArr.includes('其他')" v-model="form.targetCustomersOther" label="其他客户" placeholder="请说明" />

        <van-field v-model="form.urgency" label="时间紧迫度" is-link readonly @click="showUrgencyPicker = true"
          placeholder="请选择" />
        <van-field v-model="form.productStatus" label="竞品情况（选填）" is-link readonly @click="showProductStatusPicker = true"
          placeholder="请选择" />
        <van-field v-if="form.productStatus === '竞品已经在做了'" v-model="form.productStatusDetail" label="哪家" placeholder="请说明具体竞品" />
      </template>

      <!-- 第五步：补充材料 -->
      <template v-if="currentStep === 4">
        <div class="upload-section">
          <div class="field-label">上传附件（不限制格式和大小）</div>
          <van-uploader :after-read="onFileRead" multiple :max-count="10"
            accept="*" upload-icon="plus" result-type="file" @delete="onFileDelete" />
          <div class="hint-text">支持图片、视频、文档等所有格式</div>

          <!-- 待上传文件列表 -->
          <div v-if="pendingFiles.length" class="file-list">
            <div v-for="(f, i) in pendingFiles" :key="i" class="file-item">
              <van-icon name="description" />
              <span class="file-name">{{ f.file ? f.file.name : f.name }}</span>
              <span class="file-size">{{ f.file ? formatSize(f.file.size) : '' }}</span>
              <van-icon name="close" class="file-remove" @click="removePendingFile(i)" />
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部操作 -->
    <div class="step-footer safe-bottom">
      <van-button v-if="currentStep > 0" @click="prevStep" plain>上一步</van-button>
      <van-button @click="saveCurrentDraft" plain>暂存</van-button>
      <van-button v-if="currentStep < 4" type="primary" @click="nextStep">下一步</van-button>
      <van-button v-if="currentStep === 4" type="primary" @click="onSubmit" :loading="submitting">提交线索</van-button>
    </div>

    <!-- 线索类型选择 -->
    <van-action-sheet v-model:show="showClueTypePicker" :actions="clueTypeActions"
      @select="onSelectClueType" cancel-text="取消" />

    <!-- 可靠度选择 -->
    <van-action-sheet v-model:show="showReliabilityPicker" :actions="reliabilityActions"
      @select="onSelectReliability" cancel-text="取消" />

    <!-- 紧迫度选择 -->
    <van-action-sheet v-model:show="showUrgencyPicker" :actions="urgencyActions"
      @select="onSelectUrgency" cancel-text="取消" />

    <!-- 产品情况选择 -->
    <van-action-sheet v-model:show="showProductStatusPicker" :actions="productStatusActions"
      @select="onSelectProductStatus" cancel-text="取消" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { submitClue, saveClue, getPending, getClueDetail, uploadClueFile } from '../api'
import { showConfirmDialog } from 'vant'
import { userStore } from '../stores/user.js'

const router = useRouter()
const route = useRoute()
const currentStep = ref(0)
const submitting = ref(false)

const form = reactive({
  // 第一步
  reporterName: '',
  reporterDept: '',
  reporterContact: '',
  wecomUserId: '',
  // 第二步
  clueName: '',
  clueType: '',
  clueTypeOther: '',
  clueDesc: '',
  // 第三步
  infoSourceArr: [],
  infoSourceOther: '',
  reliability: '',
  marketSize: '',
  // 第四步
  productLinesArr: [],
  productLinesDetail: '',
  targetCustomersArr: [],
  targetCustomersOther: '',
  urgency: '',
  productStatus: '',
  productStatusDetail: '',
  // 草稿
  draftId: null
})

// 选择项配置
const clueTypeOptions = ['新产品/新功能', '现有产品改进/新应用场景', '新应用场景/新行业拓展', '产品新动向/产品威胁', '政策/标准/法规变化', '客户/市场/技术突破', '其他']
const infoSourceOptions = ['客户/业务直接反馈', '经销商/代理商反馈', '设计院/工程公司反馈', '装修公司/施工方反馈', '行业同事反馈', '行业展会/论坛/交流', '产品观察/产品情报', '行业媒体/行业报告', '政策/标准/法规文件', '其他']
const reliabilityOptions = ['高（多个独立来源可交叉验证）', '中（单一可靠来源，如老客户直接反馈）', '低（道听途说/网络偶然获取）']
const productLineOptions = ['给水管道', '排水管道', '暖通管道', '燃气管道', '新风管道', '净水/水处理', '全品类']
const customerOptions = ['房地产开发商', '施工单位', '设计院', '装修公司', '经销商', '最终用户', '工业用户', '其他']
const urgencyOptions = ['紧迫（窗口期很短，3个月内需要响应）', '较急（建议尽快关注和行动）', '从容（可从容调研，1年以上窗口期）', '不确定']
const productStatusOptions = ['竞品已经在做了', '竞品还没做，但可能在关注', '市场上还没有人做', '不清楚']

// 弹窗状态
const showClueTypePicker = ref(false)
const showReliabilityPicker = ref(false)
const showUrgencyPicker = ref(false)
const showProductStatusPicker = ref(false)

// ActionSheet 数据
const clueTypeActions = clueTypeOptions.map(v => ({ name: v }))
const reliabilityActions = reliabilityOptions.map(v => ({ name: v }))
const urgencyActions = urgencyOptions.map(v => ({ name: v }))
const productStatusActions = productStatusOptions.map(v => ({ name: v }))

// 待上传文件列表（提交时一起上传）
const pendingFiles = ref([])

// 选择器事件
const onSelectClueType = ({ name }) => {
  form.clueType = name
  showClueTypePicker.value = false
}
const onSelectReliability = ({ name }) => {
  form.reliability = name
  showReliabilityPicker.value = false
}
const onSelectUrgency = ({ name }) => {
  form.urgency = name
  showUrgencyPicker.value = false
}
const onSelectProductStatus = ({ name }) => {
  form.productStatus = name
  showProductStatusPicker.value = false
}

// 加载暂存的线索（仅在编辑已有线索时，新建模式跳过）
onMounted(async () => {
  const editId = route.query.edit
  const isNew = route.query.new === 'true'

  // 如果是全新的新建，不加载暂存线索
  if (isNew) {
    form.draftId = null
    loadWecomUserInfo()
    return
  }

  // 指定了编辑某条线索
  if (editId) {
    try {
      const { data: clueData } = await getClueDetail(editId)
      if (clueData.code === 0 && clueData.data) {
        loadClueIntoForm(clueData.data)
        showMyToast('正在编辑线索')
      }
    } catch (e) { /* ignore */ }
    loadWecomUserInfo()
    return
  }

  // 默认：尝试恢复最近的暂存线索
  try {
    const { data } = await getPending(form.wecomUserId)
    if (data.code === 0 && data.data) {
      loadClueIntoForm(data.data)
      showMyToast('已恢复暂存的线索')
    }
  } catch (e) { /* ignore */ }

  loadWecomUserInfo()
})

function loadClueIntoForm(clue) {
  form.draftId = clue.id
  form.reporterName = clue.reporterName || ''
  form.reporterDept = clue.reporterDept || ''
  form.reporterContact = clue.reporterContact || ''
  form.clueName = clue.clueName || ''
  form.clueType = clue.clueType || ''
  form.clueTypeOther = clue.clueTypeOther || ''
  form.clueDesc = clue.clueDesc || ''
  if (clue.infoSource) form.infoSourceArr = clue.infoSource.split(',')
  form.infoSourceOther = clue.infoSourceOther || ''
  form.reliability = clue.reliability || ''
  form.marketSize = clue.marketSize || ''
  if (clue.productLines) form.productLinesArr = clue.productLines.split(',')
  form.productLinesDetail = clue.productLinesDetail || ''
  if (clue.targetCustomers) form.targetCustomersArr = clue.targetCustomers.split(',')
  form.targetCustomersOther = clue.targetCustomersOther || ''
  form.urgency = clue.urgency || ''
  form.productStatus = clue.productStatus || ''
  form.productStatusDetail = clue.productStatusDetail || ''
}

function loadWecomUserInfo() {
  if (!form.reporterName) {
    form.reporterName = userStore.fullname || ''
    form.reporterContact = userStore.mobile || ''
  }
}

// 步骤验证
function validateStep(step) {
  switch (step) {
    case 0:
      if (!form.reporterName) { showMyToast('请填写报备人'); return false }
      break
    case 1:
      if (!form.clueName) { showMyToast('请填写线索名称'); return false }
      if (!form.clueType) { showMyToast('请选择线索类型'); return false }
      if (!form.clueDesc) { showMyToast('请填写线索描述'); return false }
      break
    case 2:
      if (!form.infoSourceArr.length) { showMyToast('请选择信息来源'); return false }
      if (!form.reliability) { showMyToast('请选择信息可靠度'); return false }
      if (!form.marketSize) { showMyToast('请填写预计市场规模'); return false }
      break
    case 3:
      // 非必填
      break
  }
  return true
}

// 步骤切换
function nextStep() {
  if (!validateStep(currentStep.value)) return
  currentStep.value++
  autoSaveDraft()
}

function prevStep() {
  currentStep.value--
  autoSaveDraft()
}

// 手动暂存
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

async function saveCurrentDraft() {
  const payload = buildPayload()
  payload.draftId = form.draftId
  payload.action = 'save'
  try {
    const { data } = await saveClue(payload)
    if (data.code === 0) {
      form.draftId = data.data.id
      showMyToast('暂存成功')
    } else {
      showMyToast(data.message || '暂存失败')
    }
  } catch (e) {
    showMyToast('暂存失败，请重试')
  }
}

// 自动暂存（步切换时静默调用）
async function autoSaveDraft() {
  try {
    const payload = buildPayload()
    payload.draftId = form.draftId
    payload.action = 'save'
    const { data } = await saveClue(payload)
    if (data.code === 0) {
      form.draftId = data.data.id
    }
  } catch (e) { /* silent */ }
}

function buildPayload() {
  return {
    ...form,
    infoSource: form.infoSourceArr.join(','),
    productLines: form.productLinesArr.join(','),
    targetCustomers: form.targetCustomersArr.join(','),
    action: 'submit'
  }
}

// 文件操作
function onFileRead(file) {
  // file 是 Vant Uploader 读取后的对象，包含 file 属性（原始 File 对象）
  pendingFiles.value.push(file)
}

function onFileDelete(file) {
  // Vant 删除回调
  const index = pendingFiles.value.findIndex(f => f === file)
  if (index > -1) pendingFiles.value.splice(index, 1)
}

function removePendingFile(index) {
  pendingFiles.value.splice(index, 1)
}

async function uploadFiles(clueId) {
  // 逐个上传文件到后端
  for (const item of pendingFiles.value) {
    try {
      const fileObj = item.file || item  // Vant 包装对象或原始 File
      await uploadClueFile(clueId, fileObj)
    } catch (e) {
      console.error('文件上传失败:', e)
    }
  }
}

// 提交
async function onSubmit() {
  submitting.value = true
  try {
    form.wecomUserId = userStore.userId
    const payload = buildPayload()
    const { data } = await submitClue(payload)
    if (data.code === 0) {
      // 上传附件
      if (pendingFiles.value.length > 0) {
        await uploadFiles(data.data.id)
      }
      showMyToast('提交成功！')
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

function onBack() {
  showConfirmDialog({
    title: '提示',
    message: '确定要退出吗？未提交的内容将自动保存为草稿。'
  }).then(() => {
    autoSaveDraft()
    router.back()
  }).catch(() => {})
}

function formatSize(bytes) {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(1) + ' ' + units[i]
}
</script>

<style scoped>
.submit-form {
  padding-bottom: 80px;
}
.hint-text {
  padding: 8px 16px;
  font-size: 12px;
  color: #999;
}
.field-label {
  padding: 12px 16px 8px;
  font-size: 14px;
  color: #323233;
  font-weight: 500;
}
.field-label.required::before {
  content: '* ';
  color: #ee0a24;
}
.van-checkbox-group {
  padding: 0 16px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.van-checkbox {
  margin: 0;
}
.upload-section {
  padding: 8px 16px;
}
.file-list {
  margin-top: 12px;
}
.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f7f8fa;
  border-radius: 4px;
  margin-bottom: 8px;
}
.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-size {
  color: #999;
  font-size: 12px;
  margin-right: 4px;
}
.file-remove {
  color: #ee0a24;
  font-size: 16px;
  cursor: pointer;
}

.desc-textarea-wrapper {
  margin: 0 16px;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  overflow: hidden;
}
.desc-textarea {
  width: 100%;
  min-height: 140px;
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
.desc-textarea::placeholder {
  color: #c8c9cc;
}
.desc-count {
  text-align: right;
  padding: 4px 12px 8px;
  font-size: 12px;
  color: #999;
  background: #fff;
}
</style>
