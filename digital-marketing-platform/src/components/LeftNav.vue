<template>
  <div class="crm-navigation-menu" style="width: 208px; min-width: 208px; background: #202230; height: 100vh; overflow: hidden; display: flex; flex-direction: column;">
    <!-- Logo -->
    <div class="navigation-menu-logo" style="height: 80px; display: flex; align-items: center; padding: 0 20px; border-bottom: 1px solid rgba(255,255,255,0.06);">
      <span style="font-size: 16px; font-weight: 600; color: #fff; letter-spacing: 1px;">数字化营销平台</span>
    </div>

    <!-- Menu scroll area -->
    <div style="flex: 1; overflow-y: auto; overflow-x: hidden;">
      <div v-for="module in menuModules" :key="module.name">
        <!-- Module header -->
        <div
          class="menu-module-header"
          :class="{ 'menu-module-active': module.name === '订单管理' }"
          :style="{
            height: '42px',
            lineHeight: '42px',
            padding: '0 20px',
            fontSize: '12px',
            fontWeight: '500',
            color: module.name === '订单管理' ? '#fff' : 'rgba(255,255,255,0.7)',
            cursor: 'pointer',
            background: module.name === '订单管理' ? '#1F74FF' : 'transparent',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }"
          @click="$set(module, 'expanded', !module.expanded)"
        >
          <span>{{ module.name }}</span>
          <span v-if="module.submenus.length" style="font-size: 10px; opacity: 0.5;">
            {{ module.expanded ? '▾' : '▸' }}
          </span>
        </div>

        <!-- Submenus (only for 订单管理) -->
        <div v-if="module.expanded && module.submenus.length" style="background: rgba(0,0,0,0.15);">
          <div
            v-for="sub in module.submenus"
            :key="sub"
            :style="{
              height: '40px',
              lineHeight: '40px',
              padding: '0 20px 0 38px',
              fontSize: '12px',
              fontWeight: '400',
              color: sub === '订单列表' ? '#fff' : 'rgba(255,255,255,0.7)',
              cursor: 'pointer',
              background: sub === '订单列表' ? 'rgba(31,116,255,0.3)' : 'transparent',
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }"
          >
            {{ sub }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LeftNav',
  data() {
    return {
      menuModules: [
        { name: '自定义报表', expanded: false, submenus: ['新建表格', '报表'] },
        { name: '基础配置', expanded: false, submenus: ['定价过程配置', '价目表类型配置', '凭证定价过程与订单类型关联表', '定价过程应用配置'] },
        { name: '首页', expanded: false, submenus: [] },
        { name: '入驻管理', expanded: false, submenus: ['企业列表', '测试跳转', '测试跳转外部打开', 'AIGC测试勿动'] },
        { name: '工单中心', expanded: false, submenus: ['审批及回访配置', '标签应用', '工单列表', '待回访列表', '工单问卷管理', '标签流程查询报表'] },
        { name: '企业管理', expanded: false, submenus: ['企业信息', '组织管理', 'OA组织', '部门管理', '职位管理', '平台角色管理'] },
        { name: '在线反馈', expanded: false, submenus: ['条码异常反馈', '客服回复话术维护', '条码查询结果维护', '基地及负责人维护'] },
        { name: '特殊市场支持', expanded: false, submenus: ['活动额度维护', '客户额度维护', '特殊市场支持产品维护', '服务合同数据维护'] },
        { name: '客户管理', expanded: false, submenus: ['SFA新建潜客数据报表', '经销商列表', '品牌商', '客户360', '门店'] },
        { name: '产品管理', expanded: false, submenus: ['产品列表', '产品推荐', '产品案例', '产品类别', '商品列表'] },
        { name: '区域管理', expanded: false, submenus: ['行政区域', '全国小区数据'] },
        { name: '价格表管理', expanded: false, submenus: ['价格表'] },
        { name: '服务请求', expanded: false, submenus: ['服务请求', '问题管理', '官网问题管理'] },
        { name: '发运&签收管理', expanded: false, submenus: ['交货单管理', '优速达客户月度次数配置', '优速达代理商仓库报表', '配送预约单列表'] },
        { name: '客户投诉管理', expanded: false, submenus: ['待分配工单列表', '呼叫中心工单', '客诉单', '物流投诉单'] },
        { name: '小丰物流助理', expanded: false, submenus: [] },
        { name: '费用申请', expanded: false, submenus: ['费用申请', '活动类型科目列表', '科目列表', '核销'] },
        { name: '会员管理', expanded: false, submenus: ['会员管理', '产品管理', '推广管理'] },
        { name: '信用管理', expanded: false, submenus: ['信用申请', '信用预算维护', '调差单配置表', '调差单'] },
        { name: '营销费用管理', expanded: false, submenus: ['财务科目', '业务科目', '预算列表', '月度额度', '营销方案', '营销活动', '费用申请', '费用结案'] },
        { name: '渠道报表', expanded: false, submenus: ['商机报表', '上周销量情况', '本周销量情况', '上月产品销售情况'] },
        { name: '合同管理', expanded: false, submenus: ['代理商合同'] },
        { name: '销售目标', expanded: false, submenus: ['目标管理', '目标查看', '销售目标管理', '销售目标查看'] },
        { name: '要货计划', expanded: false, submenus: ['要货计划汇总表', '要货计划提交情况表', '要货计划', '要货计划达成报表'] },
        {
          name: '订单管理',
          expanded: true,
          submenus: [
            '非常规拼团活动', '订单调整申请', '订单调整原因配置', '销售订单',
            '广告补贴占比配置', '退货订单', '发货基地列表', '公司送货客户配置',
            '定金比例配置', '到货列表', '销售订单（门店）', '退货订单（门店）',
            '天猫订单导入', '京东订单导入', '非常规定金比例配置', '订单列表',
            '促销计划列表', '工厂-仓库列表', '销量任务表', '订单全链路报表',
            '计划全链路报表', '跨季度取消记销量订单', '计划交货时间维护'
          ]
        },
        { name: '渠道进销存', expanded: false, submenus: ['仓库列表', '库存列表', '库存交易', '批次库存列表'] },
        { name: '退换货管理', expanded: false, submenus: ['退换货申请列表', '退换货初审人员配置', '退换货工厂信息配置'] },
        { name: '资金账户', expanded: false, submenus: ['月度对账单', '资金账户', '账户余额表', '计息利率维护表'] },
        { name: '返利管理', expanded: false, submenus: ['返利计提规则维护表', '返利使用规则维护表', '返利计提凭证记录表'] },
        { name: '科目管理', expanded: false, submenus: ['财务科目', '业务科目'] },
        { name: '销售政策', expanded: false, submenus: ['卫浴政策', '管道政策', '促销让利测算', '套装组合'] },
        { name: '预算管理', expanded: false, submenus: ['预算列表'] },
        { name: '渠道首页配置', expanded: false, submenus: ['首页轮播图配置'] },
        { name: '额度管理', expanded: false, submenus: ['月度额度汇总', '月度额度'] },
        { name: '营销管理', expanded: false, submenus: ['营销方案', '营销活动'] },
        { name: '费用管理', expanded: false, submenus: ['费用申请', '费用结案'] },
        { name: '拜访管理', expanded: false, submenus: ['拜访记录', '拜访日报表', '拜访数据看板', '派单任务', '拜访管理'] },
        { name: '消费者管理', expanded: false, submenus: ['消费者看板', '统一消费者', '门店消费者', '小程序用户'] },
        { name: '抽取规则配置', expanded: false, submenus: ['数据接入监控', '数据库连接配置', '数据同步映射'] },
        { name: '校验规则配置', expanded: false, submenus: ['流程-校验映射', '流程-校验逻辑', '流程-校验维护'] },
        { name: '拆分规则配置', expanded: false, submenus: ['流程-拆分映射'] },
        { name: '构建规则配置', expanded: false, submenus: ['识别信息优先级', '渠道识别信息定义', '渠道用户计算规则', '统一用户计算规则'] },
      ],
    };
  },
};
</script>
