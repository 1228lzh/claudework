# 市场机会线索提报与审核平台

企业微信 H5 应用，用于市场机会线索的提报、审核与管理。

## 技术栈

| 层 | 技术 |
|---|------|
| 前端 | Vue 3 + Vant UI 4 + Vue Router + Axios |
| 后端 | Spring Boot 2.7 + Spring Data JPA |
| 数据库 | MySQL 5.7+ |
| 构建 | Vite（前端）/ Maven（后端） |

## 项目结构

```
clue-hub/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── resources/
│       │   └── application.yml       # 应用配置（数据库、文件上传、企微）
│       └── java/com/cluehub/
│           ├── ClueHubApplication.java
│           ├── config/WebConfig.java        # CORS 跨域配置
│           ├── controller/
│           │   ├── ClueController.java      # /api/clue/*   线索提报
│           │   ├── ReviewController.java    # /api/review/* 审核管理
│           │   └── FileController.java      # /api/file/*   文件下载
│           ├── service/
│           │   ├── ClueService.java         # 线索业务（提交/暂存/查询）
│           │   ├── ReviewService.java       # 审核流转（4阶段状态机）
│           │   └── FileService.java         # 文件上传存储
│           ├── repository/                  # JPA 数据访问层
│           ├── entity/
│           │   ├── Clue.java                # 线索表
│           │   ├── ClueDraft.java           # 草稿表
│           │   ├── ReviewRecord.java        # 审核记录表
│           │   └── Attachment.java          # 附件表
│           └── dto/                         # 数据传输对象
├── frontend/                         # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js                # Vite 配置（含 API 代理）
│   ├── index.html
│   └── src/
│       ├── main.js                   # 入口
│       ├── App.vue                   # 根组件（响应式容器）
│       ├── router/index.js           # 路由配置
│       ├── api/index.js              # Axios API 封装
│       ├── assets/common.css         # 全局样式 + 状态标签
│       └── views/
│           ├── UserHome.vue          # 用户端：线索提报 + 我的
│           ├── SubmitForm.vue        # 5步分步提报表单
│           ├── ClueDetail.vue        # 线索详情 + 审核历史
│           ├── AdminHome.vue         # 管理端：线索管理 + 我的
│           └── AdminDetail.vue       # 审核详情 + 操作
└── README.md
```

## 状态体系

```
提交 → 初筛中 → 研判中 → 验证中 → IPD立项（终态）
         ↓        ↓        ↓
     退回/不通过  不通过    不通过
```

| 状态值 | 显示 | 说明 |
|--------|------|------|
| `new` | 新建 | 暂存/退回补充后 |
| `initial_screening` | 初筛中 | 可退/可过/可不通过 |
| `judging` | 研判中 | 可过/可不通过 |
| `verifying` | 验证中 | 可过/可不通过 |
| `ipd_review` | IPD立项 | 终态 |
| `{stage}_rejected` | 各阶段不通过 | 终态 |

## 本地开发

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 18+
- MySQL 5.7+

### 1. 数据库

创建数据库（JPA 自动建表）：

```sql
CREATE DATABASE IF NOT EXISTS clue_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

修改 `backend/src/main/resources/application.yml` 中的数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/clue_hub?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
# 启动在 http://localhost:8080
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 启动在 http://localhost:3000
```

前端开发服务器已配置 API 代理，`/api/*` 请求自动转发到 `http://localhost:8080`。

### 4. 访问

| 页面 | URL | 用途 |
|------|-----|------|
| 用户端 | http://localhost:3000/ | 线索提报 + 我的 |
| 管理端 | http://localhost:3000/admin | 审核管理 |
| 提报表单 | http://localhost:3000/submit | 5步分步表单 |

## 企微集成

当前使用模拟用户数据（`wecomUserId: user_001`）。实际对接需：

1. 配置 `application.yml` 中企微参数（corp-id、agent-id、secret）
2. 前端接入企微 JS-SDK OAuth 静默授权
3. 替换 `UserHome.vue` 和 `SubmitForm.vue` 中的模拟用户信息

## 部署

```bash
# 前端构建
cd frontend && npm run build    # 产物在 dist/

# 后端打包
cd backend && mvn package -DskipTests  # 产物在 target/clue-hub-1.0.0.jar

# 运行
java -jar target/clue-hub-1.0.0.jar
```

前端 `dist/` 目录可部署到 Nginx，或放入 Spring Boot `static/` 目录。
