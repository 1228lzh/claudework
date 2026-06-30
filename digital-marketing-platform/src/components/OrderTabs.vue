<template>
  <div style="background: #FFFFFF; border-radius: 2px;">
    <!-- Tabs -->
    <div style="display: flex; border-bottom: 1px solid #E8EAED;">
      <div v-for="tab in tabs" :key="tab.label"
        :style="{
          padding: '12px 20px', fontSize: '14px', fontWeight: tab.active ? '600' : '400',
          color: tab.active ? '#1F74FF' : '#4E596A', cursor: 'pointer',
          borderBottom: tab.active ? '2px solid #1F74FF' : '2px solid transparent',
          marginBottom: '-1px',
        }"
        @click="$emit('update:activeTab', tab.label)"
      >
        {{ tab.label }}
        <span v-if="tab.count !== undefined" style="margin-left: 4px; font-size: 12px; color: #B3B6BF;">({{ tab.count }})</span>
      </div>
    </div>

    <!-- 购买的产品 Tab -->
    <div v-if="activeTab === '购买的产品'" style="padding: 0; overflow: hidden;">
      <ProductTable />
    </div>

    <!-- 运输方式变更附件 Tab -->
    <div v-else-if="activeTab === '运输方式变更附件'" style="padding: 20px;">
      <div style="max-width: 500px;">
        <!-- 申请原因 -->
        <div style="display: flex; align-items: flex-start; margin-bottom: 16px;">
          <span style="color: #4E596A; width: 105px; text-align: right; margin-right: 8px; flex-shrink: 0; white-space: nowrap; font-size: 12px; line-height: 28px;">
            <span style="color: #FF4D4F;">*</span>申请原因：
          </span>
          <select v-model="selectedReason"
            :style="{
              width: '380px', height: '28px', padding: '0 8px',
              border: '1px solid #DCE0E6', borderRadius: '2px',
              fontSize: '12px', color: '#838C99', background: '#FFFFFF',
              outline: 'none', cursor: 'pointer',
            }"
          >
            <option value="" disabled>请选择申请原因</option>
            <option v-for="option in reasonOptions" :key="option" :value="option">{{ option }}</option>
          </select>
        </div>

        <!-- 备注 -->
        <div style="display: flex; align-items: center; margin-bottom: 16px;">
          <span style="color: #4E596A; width: 105px; text-align: right; margin-right: 8px; flex-shrink: 0; white-space: nowrap; font-size: 12px;">
            <span v-if="selectedReason === '其他'" style="color: #FF4D4F;">*</span>其他原因备注：
          </span>
          <textarea
            v-model="remark"
            :disabled="selectedReason !== '其他'"
            :style="{
              width: '300px', height: '80px', padding: '6px 8px',
              border: '1px solid #DCE0E6', borderRadius: '2px',
              fontSize: '12px', color: '#838C99',
              background: selectedReason === '其他' ? '#FFFFFF' : '#F0F2F7',
              outline: 'none', resize: 'vertical',
            }"
            :placeholder="selectedReason === '其他' ? '请输入' : ''"
          ></textarea>
        </div>

        <!-- 附件 -->
        <div style="display: flex; align-items: flex-start; margin-bottom: 16px;">
          <span style="color: #4E596A; width: 105px; text-align: right; margin-right: 8px; flex-shrink: 0; white-space: nowrap; font-size: 12px; line-height: 80px;">
            <span style="color: #FF4D4F;">*</span>申请附件：
          </span>
          <div style="flex: 1; max-width: 250px;">
            <input type="file" ref="fileInput"
              style="display: none;"
              @change="onFileChange"
              multiple
            />
            <!-- 上传区域 -->
            <div @click="$refs.fileInput.click()" @dragover.prevent @drop.prevent="onDrop"
              :style="{
                width: '100%', height: '80px', border: '1px dashed #DCE0E6',
                borderRadius: '2px', display: 'flex', flexDirection: 'column',
                alignItems: 'center', justifyContent: 'center',
                cursor: 'pointer', background: '#FAFAFA', marginBottom: fileList.length ? '8px' : '0',
              }"
            >
              <span style="font-size: 28px; color: #DCE0E6; line-height: 1;">+</span>
            </div>
            <!-- 已选文件列表 -->
            <div v-if="fileList.length" style="border: 1px solid #E8EAED; border-radius: 2px; overflow: hidden;">
              <div v-for="(f, i) in fileList" :key="i"
                :style="{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  padding: '6px 12px', fontSize: '12px', color: '#1D222A',
                  background: i % 2 === 0 ? '#FFFFFF' : '#FAFAFA',
                  borderBottom: i < fileList.length - 1 ? '1px solid #F0F2F7' : 'none',
                }"
              >
                <div style="display: flex; align-items: center; gap: 6px;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#4E596A" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                  <span>{{ f.name }}</span>
                  <span style="color: #B3B6BF;">{{ formatSize(f.size) }}</span>
                </div>
                <button @click="removeFile(i)"
                  style="background: none; border: none; cursor: pointer; color: #FF4D4F; font-size: 14px; padding: 0; line-height: 1;"
                >✕</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 其他空 Tab -->
    <div v-else style="padding: 40px; text-align: center; color: #B3B6BF; font-size: 14px;">
      暂无数据
    </div>
  </div>
</template>

<script>
import ProductTable from './ProductTable.vue';

export default {
  name: 'OrderTabs',
  components: { ProductTable },
  props: {
    activeTab: { type: String, default: '购买的产品' },
    transportMethod: { type: String, default: '物流配送' },
  },
  emits: ['update:activeTab'],
  data() {
    return {
      selectedReason: '',
      remark: '',
      fileList: [],
      reasonOptions: [
        '商机报备项目需求自提',
        '商机报备项目需求配送',
        '非默认基地发货申请配送',
        '工程急单改自提（15m³以内），零担时效无法满足',
        '客户申请配送零散订单随自提专车发运',
        '节假日物流停运零散订单申请自提',
        '当天预约整车加急提货，公司配送无法满足申请',
        '其他',
      ],
    };
  },
  methods: {
    onFileChange(e) {
      const files = Array.from(e.target.files);
      files.forEach(f => {
        this.fileList.push({
          name: f.name,
          size: f.size,
          raw: f,
        });
      });
    },
    onDrop(e) {
      const files = Array.from(e.dataTransfer.files);
      files.forEach(f => {
        this.fileList.push({
          name: f.name,
          size: f.size,
          raw: f,
        });
      });
    },
    removeFile(idx) {
      this.fileList.splice(idx, 1);
    },
    formatSize(bytes) {
      if (bytes < 1024) return bytes + 'B';
      if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB';
      return (bytes / (1024 * 1024)).toFixed(1) + 'MB';
    },
  },
  watch: {
    selectedReason(val) {
      if (val !== '其他') {
        this.remark = '';
      }
    },
  },
  computed: {
    tabs() {
      return [
        { label: '购买的产品', count: 9, active: this.activeTab === '购买的产品' },
        { label: '评审附件', count: 0, active: this.activeTab === '评审附件' },
        { label: '运输方式变更附件', count: 0, active: this.activeTab === '运输方式变更附件' },
        { label: '变更记录', count: 0, active: this.activeTab === '变更记录' },
        { label: '单据流', count: 0, active: this.activeTab === '单据流' },
      ];
    },
  },
};
</script>
