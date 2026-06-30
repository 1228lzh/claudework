<template>
  <div style="background: #FFFFFF; border-radius: 2px; padding: 20px; margin-bottom: 12px;">
    <!-- Title + steps -->
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
      <h2 style="font-size: 16px; font-weight: 600; color: #1D222A;">订单详情</h2>
      <div style="display: flex; gap: 8px;">
        <template v-if="isEditing">
          <button
            style="height: 26px; padding: 0 8px; borderRadius: 2px; border: none; background: #EBF6FF; color: #1F74FF; fontSize: 12px; cursor: pointer; minWidth: 72px; display: flex; alignItems: center; justifyContent: center;"
            @click="$emit('update:isEditing', false)"
          >取消</button>
          <button
            style="height: 26px; padding: 0 8px; borderRadius: 2px; border: none; background: #EBF6FF; color: #1F74FF; fontSize: 12px; cursor: pointer; minWidth: 72px; display: flex; alignItems: center; justifyContent: center;"
            @click="$emit('update:isEditing', false)"
          >提交</button>
          <button
            style="height: 26px; padding: 0 8px; borderRadius: 2px; border: none; background: '#EBF6FF'; color: #1F74FF; fontSize: 12px; cursor: pointer; minWidth: 72px; display: flex; alignItems: center; justifyContent: center;"
            @click="$emit('update:isEditing', false)"
          >返回</button>
        </template>
        <template v-else>
          <button v-for="btn in normalButtons" :key="btn.label"
            :style="{
              height: '26px', padding: '0 8px', borderRadius: '2px', border: 'none',
              background: btn.label === '返回' ? '#EBF6FF' : '#1F74FF',
              color: btn.label === '返回' ? '#1F74FF' : '#ECF5FF',
              fontSize: '12px', cursor: 'pointer', display: 'flex', alignItems: 'center',
              minWidth: '72px', justifyContent: 'center',
            }"
            @click="btn.label === '订单变更' ? $emit('update:isEditing', true) : null"
          >
            {{ btn.label }}
          </button>
        </template>
      </div>
    </div>

    <!-- Steps -->
    <div style="display: flex; align-items: flex-start; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #F0F2F7; width: calc(105px * 3 + 8px * 3 + 250px * 3 + 24px * 2);">
      <div v-for="(step, idx) in steps" :key="step.label"
        :style="{
          display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px',
          flex: '1', position: 'relative',
        }"
      >
        <div v-if="idx < steps.length - 1" :style="{
          position: 'absolute', top: '18px', left: 'calc(50% + 18px)', right: 'calc(-50% + 18px)', height: '2px',
          background: step.done ? '#1F74FF' : '#E8EAED',
        }"></div>
        <div :style="{
          width: '36px', height: '36px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '14px', fontWeight: '700',
          background: step.active || step.done ? '#1F74FF' : '#F0F2F7',
          color: step.active || step.done ? '#fff' : '#B3B6BF',
          zIndex: '1',
        }">{{ idx + 1 }}</div>
        <span :style="{
          fontSize: '12px',
          color: step.active ? '#1D222A' : (step.done ? '#1D222A' : '#B3B6BF'),
          fontWeight: step.active ? '700' : (step.done ? '600' : '400'),
          textAlign: 'center',
        }">{{ step.label }}</span>
      </div>
    </div>

    <!-- Basic info fields - 3 columns -->
    <div style="display: grid; grid-template-columns: repeat(3, max-content); gap: 0 24px; font-size: 12px;">
      <div v-for="field in infoFields" :key="field.label"
        style="display: flex; align-items: center; padding: 6px 0; min-height: 32px;"
        v-show="field.label !== '基本信息'"
      >
        <span style="color: #4E596A; width: 105px; text-align: right; margin-right: 8px; flex-shrink: 0; white-space: nowrap;">
          <span v-if="field.required" style="color: #FF4D4F; margin-right: 2px;">*</span>{{ field.label }}：
        </span>
        <!-- 运输方式：编辑模式下变为下拉 -->
        <select v-if="field.label === '运输方式' && isEditing"
          :value="transportMethod"
          @change="$emit('update:transportMethod', $event.target.value)"
          :style="{
            width: '250px', height: '28px', padding: '0 8px',
            border: '1px solid #DCE0E6', borderRadius: '2px',
            fontSize: '12px', color: '#838C99', background: '#FFFFFF',
            outline: 'none', cursor: 'pointer',
          }"
        >
          <option value="物流配送">物流配送</option>
          <option value="客户自提">客户自提</option>
        </select>
        <input v-else
          :value="field.value"
          readonly
          :style="{
            width: '250px',
            height: '28px',
            padding: '0 8px',
            border: '1px solid #DCE0E6',
            borderRadius: '2px',
            fontSize: '12px',
            color: '#838C99',
            background: '#F0F2F7',
            outline: 'none',
          }"
        />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderHeader',
  props: {
    isEditing: { type: Boolean, default: false },
    transportMethod: { type: String, default: '物流配送' },
  },
  data() {
    return {
      normalButtons: [
        { label: '二次转单' },
        { label: '运费测算' },
        { label: '体积测算' },
        { label: '订单变更' },
        { label: '整单取消' },
        { label: '返回' },
      ],
      steps: [
        { label: '订单创建', done: true },
        { label: '订单审核', done: true },
        { label: '待确认订单', done: true },
        { label: '待交货', active: true },
        { label: '待预约出库', done: false },
        { label: '待签收确认', done: false },
        { label: '已完成', done: false },
      ],
      infoFields: [
        { label: '基本信息', value: '', required: false },
        { label: '订单号', value: 'SF-2026-001234', required: false },
        { label: '订单类型', value: '订单-常规-MTS', required: false },
        { label: '状态', value: '待交货', required: false },
        { label: '创建日期', value: '2026-06-18 11:00:51', required: false },
        { label: 'ERP单号', value: 'ERP20260618001', required: false },
        { label: '拼团单号', value: '-', required: false },
        { label: '客户编码', value: 'CUS202405001', required: true },
        { label: '客户名称', value: '佛山市顺德区经销商有限公司', required: false },
        { label: '销售组织', value: '华南营销中心', required: false },
        { label: '销售区域', value: '广东省-佛山市', required: false },
        { label: '促销活动', value: '-', required: false },
        { label: '定价日期', value: '2026-06-18', required: false },
        { label: '信用状态', value: '正常', required: false },
        { label: '是否提前单', value: '否', required: false },
        { label: '运输方式', value: '物流配送', required: true },
        { label: '请求交货日期', value: '2026-07-01', required: true },
        { label: '地址简称', value: '总部成品仓', required: true },
        { label: '收货地址', value: '广东省佛山市三水区乐平镇日丰工业园', required: false },
        { label: '总体积(M3)', value: '72.50', required: false },
        { label: '总重量(KG)', value: '1520.00', required: false },
        { label: '货物总额(元)', value: '¥ 126,936.58', required: true },
        { label: '实付金额(元)', value: '¥ 88,855.61', required: true },
        { label: '开票金额(元)', value: '¥ 88,855.61', required: true },
        { label: '奖励/返利类型', value: '-', required: false },
        { label: '是否定制', value: '否', required: false },
        { label: '拼单号', value: '-', required: false },
        { label: '特殊支持', value: '-', required: false },
        { label: '是否免运', value: '否', required: false },
        { label: '自动转单', value: '否', required: false },
        { label: '计销量', value: '是', required: false },
        { label: '优速达', value: '-', required: false },
      ],
    };
  },
};
</script>
