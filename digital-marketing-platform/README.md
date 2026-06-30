# 数字化营销平台

基于 Vue 2 + Tailwind CSS 构建的企业数字化营销 CRM 系统原型。

## 技术栈

- **Vue 2.6** — 渐进式前端框架
- **Tailwind CSS 3** — 实用优先的 CSS 框架
- **Vue CLI 5** — 构建工具链
- **PostCSS + Autoprefixer** — CSS 处理

## 功能模块

| 模块 | 说明 |
|------|------|
| 左侧导航 | 30+ 业务模块，支持多级菜单展开 |
| 订单详情 | 订单基本信息、流程步骤、编辑/取消操作 |
| 产品表格 | 42 列产品数据，支持搜索、导出、分页 |
| 运输方式变更 | 申请原因、附件上传（拖拽/选择） |
| 变更记录 / 单据流 | Tab 占位，待实现 |

## 项目结构

```
digital-marketing-platform/
├── public/
│   └── index.html
├── src/
│   ├── App.vue                      # 根组件
│   ├── main.js                      # 入口
│   ├── assets/
│   │   └── tailwind.css             # Tailwind 基础样式
│   └── components/
│       ├── LeftNav.vue              # 左侧导航菜单
│       ├── TopBar.vue               # 顶部栏
│       ├── OrderDetail.vue          # 订单详情容器
│       ├── OrderHeader.vue          # 订单头部信息
│       ├── OrderTabs.vue            # 选项卡切换
│       └── ProductTable.vue         # 产品数据表格
├── postcss.config.js
├── tailwind.config.js
├── vue.config.js                    # 输出到 dist-proto
└── package.json
```

## 开发

```bash
npm install
npm run dev     # 启动开发服务器
npm run build   # 构建到 dist-proto/
```
