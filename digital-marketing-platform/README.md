# 数字化营销平台

基于 Vue 2 + Tailwind CSS 的企业数字化营销 CRM 系统原型。

## 开发

```bash
npm install
npm run dev     # 启动开发服务器
npm run build   # 构建到 dist-proto/
```

## 技术栈

Vue 2.6 · Tailwind CSS 3 · Vue CLI 5 · PostCSS

## 项目结构

```
src/
├── App.vue                 # 根组件
├── main.js                 # 入口
├── assets/tailwind.css     # Tailwind 样式
└── components/
    ├── LeftNav.vue         # 左侧导航
    ├── TopBar.vue          # 顶部栏
    ├── OrderDetail.vue     # 订单详情
    ├── OrderHeader.vue     # 订单头部
    ├── OrderTabs.vue       # Tab 切换
    └── ProductTable.vue    # 产品数据表格
```
