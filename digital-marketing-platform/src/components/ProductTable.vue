<template>
  <div>
    <!-- Search bar -->
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-bottom: 1px solid #F0F2F7;">
      <div style="display: flex; align-items: center; gap: 8px;">
        <div style="position: relative;">
          <input v-model="searchText" placeholder="搜索..."
            style="width: 200px; height: 32px; padding: 0 12px; border: 1px solid #D9D9D9; border-radius: 2px; font-size: 12px; outline: none;"
          />
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#B3B6BF" stroke-width="2" style="position: absolute; right: 8px; top: 9px;">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
        </div>
        <button style="height: 32px; padding: 0 16px; background: #1F74FF; color: #fff; border: none; border-radius: 2px; font-size: 12px; cursor: pointer;">查询</button>
      </div>
      <div style="display: flex; align-items: center; gap: 8px;">
        <button style="height: 32px; padding: 0 12px; background: #fff; color: #4E596A; border: 1px solid #D9D9D9; border-radius: 2px; font-size: 12px; cursor: pointer;">取消订单行</button>
        <button style="height: 32px; padding: 0 12px; background: #fff; color: #4E596A; border: 1px solid #D9D9D9; border-radius: 2px; font-size: 12px; cursor: pointer; display: flex; align-items: center; gap: 4px;">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
          导出
        </button>
      </div>
    </div>

    <!-- Scrollable table wrapper -->
    <div style="overflow-x: auto;">
      <table style="width: 100%; min-width: 2400px; border-collapse: collapse; font-size: 12px;">
        <!-- Table header -->
        <thead>
          <tr style="background: #FAFAFA; border-bottom: 1px solid #E8EAED;">
            <th v-for="col in columns" :key="col.key"
              :style="{
                padding: '10px 8px', textAlign: 'left', whiteSpace: 'nowrap',
                fontWeight: '500', color: '#1D222A', fontSize: '12px',
                minWidth: col.width || 'auto', position: 'sticky', top: 0,
                background: '#FAFAFA',
              }"
            >
              {{ col.label }}
            </th>
          </tr>
        </thead>
        <!-- Table body -->
        <tbody>
          <tr v-for="(row, idx) in tableData" :key="idx"
            :style="{ background: idx % 2 === 0 ? '#FFFFFF' : '#FAFAFA', borderBottom: '1px solid #F0F2F7' }"
          >
            <td v-for="col in columns" :key="col.key"
              :style="{
                padding: '10px 8px', whiteSpace: 'nowrap', fontSize: '12px',
                color: col.key === 'rowNum' ? '#4E596A' : '#1D222A',
                textAlign: col.key === 'rowNum' ? 'center' : 'left',
              }"
            >
              {{ row[col.key] }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Footer pagination -->
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid #E8EAED;">
      <div style="font-size: 12px; color: #4E596A;">
        当前显示 ，共 <span style="color: #1D222A; font-weight: 500;">9</span> 条记录
      </div>
      <div style="display: flex; align-items: center; gap: 4px;">
        <select style="height: 28px; padding: 0 8px; border: 1px solid #D9D9D9; border-radius: 2px; font-size: 12px; color: #4E596A;">
          <option>10条/页</option>
          <option>20条/页</option>
          <option>50条/页</option>
        </select>
        <button style="width: 28px; height: 28px; border: 1px solid #D9D9D9; border-radius: 2px; background: #fff; color: #B3B6BF; cursor: pointer; display: flex; align-items: center; justify-content: center;">◂</button>
        <span style="display: flex; align-items: center; justify-content: center; width: 28px; height: 28px; background: #1F74FF; color: #fff; border-radius: 2px; font-size: 12px; font-weight: 500;">1</span>
        <button style="width: 28px; height: 28px; border: 1px solid #D9D9D9; border-radius: 2px; background: #fff; color: #B3B6BF; cursor: pointer; display: flex; align-items: center; justify-content: center;">▸</button>
        <span style="font-size: 12px; color: #4E596A; margin: 0 4px;">前往</span>
        <input style="width: 40px; height: 28px; border: 1px solid #D9D9D9; border-radius: 2px; text-align: center; font-size: 12px;" value="1" />
        <span style="font-size: 12px; color: #4E596A;">页</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductTable',
  data() {
    return {
      searchText: '',
      columns: [
        { key: 'rowNum', label: '#', width: '40px' },
        { key: 'project', label: '项目', width: '100px' },
        { key: 'productCode', label: '产品编码', width: '120px' },
        { key: 'productName', label: '产品名称', width: '200px' },
        { key: 'setName', label: '所属套装', width: '120px' },
        { key: 'qty', label: '数量', width: '80px' },
        { key: 'unit', label: '销售单位', width: '80px' },
        { key: 'unitPrice', label: '单价(元)', width: '100px' },
        { key: 'subtotal', label: '小计', width: '100px' },
        { key: 'settlePrice', label: '结算价(元)', width: '100px' },
        { key: 'settleAmount', label: '结算金额', width: '100px' },
        { key: 'invoicePrice', label: '含税开票价(元)', width: '120px' },
        { key: 'invoiceAmount', label: '含税开票金额', width: '120px' },
        { key: 'discount', label: '折扣金额(元)', width: '100px' },
        { key: 'deposit', label: '定金金额(元)', width: '100px' },
        { key: 'projectType', label: '项目类别', width: '100px' },
        { key: 'customLength', label: '定制长度(米)', width: '100px' },
        { key: 'customReq', label: '定制要求', width: '100px' },
        { key: 'planDelivery', label: '计划交货时间', width: '120px' },
        { key: 'latestDelivery', label: '最新计划交货时间', width: '140px' },
        { key: 'reviewOpinion', label: '交货生产评审意见', width: '140px' },
        { key: 'factory', label: '发货工厂', width: '120px' },
        { key: 'warehouse', label: '仓库地点', width: '120px' },
        { key: 'modifyReason', label: '修改工厂原因', width: '120px' },
        { key: 'promoRule', label: '促销规则', width: '100px' },
        { key: 'configInfo', label: '选配信息', width: '100px' },
        { key: 'customFile', label: '定制文件', width: '100px' },
        { key: 'isGift', label: '是否赠品', width: '80px' },
        { key: 'orderStatus', label: '订单行状态', width: '100px' },
        { key: 'cancelReason', label: '取消原因', width: '100px' },
        { key: 'priceDate', label: '定价日期', width: '100px' },
        { key: 'orderQty', label: '下单数量', width: '80px' },
        { key: 'canceledQty', label: '已取消数量', width: '80px' },
        { key: 'deliveredQty', label: '已交货数量', width: '80px' },
        { key: 'scheduledQty', label: '已预约数量', width: '80px' },
        { key: 'postedQty', label: '交货过账数量', width: '100px' },
        { key: 'invoicePostedQty', label: '开票过账数量', width: '100px' },
        { key: 'shippedQty', label: '已发货数量', width: '80px' },
        { key: 'receivedQty', label: '已签收数量', width: '80px' },
        { key: 'productCategory', label: '产品分类', width: '100px' },
        { key: 'weight', label: '重量', width: '80px' },
        { key: 'volume', label: '体积', width: '80px' },
      ],
      tableData: [
        { rowNum: '1', project: '10', productCode: '1100001866', productName: 'PVC-GY.205-16x1.0-120(3x40)-红色', setName: '-', qty: '1200', unit: '米', unitPrice: '￥0.581315', subtotal: '￥697.58', settlePrice: '￥0.406923', settleAmount: '￥488.31', invoicePrice: '￥0.406923', invoiceAmount: '￥488.31', discount: '￥209.27', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '1200', canceledQty: '0', deliveredQty: '1200', scheduledQty: '0', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '102.000', volume: '0.300' },
        { rowNum: '2', project: '20', productCode: '1100002138', productName: 'PVC-U-75x2.3-4-白色', setName: '-', qty: '400', unit: '米', unitPrice: '￥78.347785', subtotal: '￥31,339.11', settlePrice: '￥54.843460', settleAmount: '￥21,937.38', invoicePrice: '￥54.843460', invoiceAmount: '￥21,937.38', discount: '￥9,401.73', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '新材三水仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '400', canceledQty: '0', deliveredQty: '400', scheduledQty: '0', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '367.600', volume: '10.000' },
        { rowNum: '3', project: '30', productCode: '1100002183', productName: '三水PVC-U-50x2.0-4-Ⅰ-白色', setName: '-', qty: '400', unit: '米', unitPrice: '￥3.483275', subtotal: '￥1,393.31', settlePrice: '￥2.438300', settleAmount: '￥975.32', invoicePrice: '￥2.438300', invoiceAmount: '￥975.32', discount: '￥417.99', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '新材三水仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '400', canceledQty: '0', deliveredQty: '400', scheduledQty: '0', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '238.800', volume: '1.000' },
        { rowNum: '4', project: '40', productCode: '1100002262', productName: '三水PVC-U-110x2.8-4-Ⅲ-白色', setName: '-', qty: '400', unit: '米', unitPrice: '￥97.672144', subtotal: '￥39,068.86', settlePrice: '￥68.370494', settleAmount: '￥27,348.20', invoicePrice: '￥68.370494', invoiceAmount: '￥27,348.20', discount: '￥11,720.66', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '新材三水仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '400', canceledQty: '0', deliveredQty: '400', scheduledQty: '0', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '719.200', volume: '4.700' },
        { rowNum: '5', project: '50', productCode: '1100015584', productName: '日丰（888）高端酸性玻璃胶（透明）-240g', setName: '-', qty: '240', unit: '支', unitPrice: '￥37.041002', subtotal: '￥8,889.84', settlePrice: '￥25.928710', settleAmount: '￥6,222.89', invoicePrice: '￥25.928710', invoiceAmount: '￥6,222.89', discount: '￥2,666.95', deposit: '￥0.00', projectType: '订单-非常规-MTO', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-07-01', orderQty: '240', canceledQty: '0', deliveredQty: '240', scheduledQty: '240', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '77.520', volume: '0.150' },
        { rowNum: '6', project: '60', productCode: '1100039338', productName: '等径三通F12-T32x32x32（家装精品）（黄）', setName: '-', qty: '960', unit: '套', unitPrice: '￥10.453471', subtotal: '￥10,035.33', settlePrice: '￥7.317429', settleAmount: '￥7,024.73', invoicePrice: '￥7.317429', invoiceAmount: '￥7,024.73', discount: '￥3,010.60', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '960', canceledQty: '0', deliveredQty: '960', scheduledQty: '960', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '79.080', volume: '0.460' },
        { rowNum: '7', project: '70', productCode: '1100039339', productName: '等径弯头F12-L32x32（45°）（家装精品）（黄）', setName: '-', qty: '1600', unit: '套', unitPrice: '￥7.310079', subtotal: '￥11,696.13', settlePrice: '￥5.117054', settleAmount: '￥8,187.29', invoicePrice: '￥5.117054', invoiceAmount: '￥8,187.29', discount: '￥3,508.84', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '1600', canceledQty: '0', deliveredQty: '1600', scheduledQty: '0', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '78.800', volume: '0.460' },
        { rowNum: '8', project: '80', productCode: '1100039363', productName: '异径弯头F12-L32x20（家装精品）（黄）', setName: '-', qty: '2400', unit: '套', unitPrice: '￥8.943029', subtotal: '￥21,463.27', settlePrice: '￥6.260121', settleAmount: '￥15,024.29', invoicePrice: '￥6.260121', invoiceAmount: '￥15,024.29', discount: '￥6,438.98', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '2400', canceledQty: '0', deliveredQty: '2400', scheduledQty: '2400', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '91.600', volume: '0.460' },
        { rowNum: '9', project: '90', productCode: '1100039365', productName: '异径直通F12-S32x20（家装精品）（黄）', setName: '-', qty: '3190', unit: '套', unitPrice: '￥4.689866', subtotal: '￥14,960.67', settlePrice: '￥3.282907', settleAmount: '￥10,472.47', invoicePrice: '￥3.282907', invoiceAmount: '￥10,472.47', discount: '￥4,488.20', deposit: '￥0.00', projectType: '订单-常规-MTS', customLength: '-', customReq: '-', planDelivery: '-', latestDelivery: '-', reviewOpinion: '-', factory: '总部成品仓', warehouse: '-', modifyReason: '-', promoRule: '-', configInfo: '-', customFile: '-', isGift: '否', orderStatus: '全部交货', cancelReason: '-', priceDate: '2026-06-18', orderQty: '3190', canceledQty: '0', deliveredQty: '3190', scheduledQty: '3190', postedQty: '0', invoicePostedQty: '0', shippedQty: '0', receivedQty: '0', productCategory: '-', weight: '101.881', volume: '0.459' },
      ],
    };
  },
};
</script>
