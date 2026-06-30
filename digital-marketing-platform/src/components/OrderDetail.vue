<template>
  <div style="flex: 1; overflow: auto; background: #F0F2F7; display: flex; flex-direction: column;">
    <TopBar />
    <div style="flex: 1; padding: 16px; overflow: auto;">
      <OrderHeader :isEditing.sync="isEditing" :transportMethod.sync="transportMethod" />
      <OrderTabs :activeTab.sync="activeTab" :transportMethod="transportMethod" />
    </div>
  </div>
</template>

<script>
import TopBar from './TopBar.vue';
import OrderHeader from './OrderHeader.vue';
import OrderTabs from './OrderTabs.vue';

export default {
  name: 'OrderDetail',
  components: { TopBar, OrderHeader, OrderTabs },
  data() {
    return {
      isEditing: false,
      transportMethod: '物流配送',
      activeTab: '购买的产品',
    };
  },
  watch: {
    transportMethod(val) {
      if (val === '客户自提') {
        this.activeTab = '运输方式变更附件';
      }
    },
  },
};
</script>
