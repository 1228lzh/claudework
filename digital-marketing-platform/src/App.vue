<template>
  <div class="crm-navigation" style="height: 100vh; display: flex;">
    <LeftNav />
    <OrderDetail />
  </div>
</template>

<script>
import LeftNav from './components/LeftNav.vue';
import OrderDetail from './components/OrderDetail.vue';

export default {
  name: 'App',
  components: { LeftNav, OrderDetail },
};
</script>
