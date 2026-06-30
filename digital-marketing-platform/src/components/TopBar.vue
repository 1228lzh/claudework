<template>
  <div style="height: 56px; background: #FFFFFF; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; border-bottom: 1px solid #E8EAED; margin: -16px -16px 16px -16px;">
    <!-- Breadcrumb + title -->
    <div style="display: flex; align-items: center; gap: 8px;">
      <label style="font-size: 14px; color: #4E596A; font-weight: 400;">订单管理</label>
      <span style="color: #B3B6BF;">/</span>
      <div style="font-size: 14px; color: #1D222A; font-weight: 500;">订单列表</div>
    </div>
    <!-- User info -->
    <div style="display: flex; align-items: center; gap: 12px;">
      <span class="nav-download-link" style="display: flex; align-items: center; gap: 4px; cursor: pointer; color: #4E596A; font-size: 12px;">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        下载
      </span>
      <div style="position: relative; cursor: pointer;">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#4E596A" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        <span style="position: absolute; top: -4px; right: -8px; background: #FF4D4F; color: #fff; font-size: 10px; padding: 0 4px; border-radius: 8px; line-height: 16px;">99+</span>
      </div>
      <span style="font-size: 12px; color: #1D222A;">林泽海&nbsp;&nbsp;IT管理员50002062</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TopBar',
};
</script>
