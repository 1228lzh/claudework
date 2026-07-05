# 线索提报与审核平台 - 建表语句

> 数据库：MySQL 5.7+ ｜ 字符集：utf8mb4

## 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS clue_hub
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE clue_hub;
```

## 1. 线索表 (clue)

```sql
CREATE TABLE IF NOT EXISTS clue (
  id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
  clue_no         VARCHAR(20)     NOT NULL                 COMMENT '线索编号，如 CL202607030001',
  -- 第一步：谁报的
  reporter_name   VARCHAR(50)     DEFAULT NULL             COMMENT '报备人',
  reporter_dept   VARCHAR(100)    DEFAULT NULL             COMMENT '部门/单位',
  reporter_contact VARCHAR(50)    DEFAULT NULL             COMMENT '联系方式',
  wecom_user_id   VARCHAR(64)     DEFAULT NULL             COMMENT '企微/飞连 userid',
  -- 第二步：什么线索
  clue_name       VARCHAR(100)    DEFAULT NULL             COMMENT '线索名称',
  clue_type       VARCHAR(100)    DEFAULT NULL             COMMENT '线索类型',
  clue_type_other VARCHAR(100)    DEFAULT NULL             COMMENT '线索类型-其他说明',
  clue_desc       TEXT                                     COMMENT '线索描述',
  -- 第三步：线索来源
  info_source     VARCHAR(500)    DEFAULT NULL             COMMENT '信息来源，多个逗号分隔',
  info_source_other VARCHAR(100)  DEFAULT NULL             COMMENT '信息来源-其他说明',
  reliability     VARCHAR(100)    DEFAULT NULL             COMMENT '信息可靠度：高/中/低',
  market_size     VARCHAR(200)    DEFAULT NULL             COMMENT '预计市场规模',
  -- 第四步：线索判断
  product_lines   VARCHAR(500)    DEFAULT NULL             COMMENT '涉及品类，多个逗号分隔',
  product_lines_detail VARCHAR(100) DEFAULT NULL           COMMENT '全品类-具体说明',
  target_customers VARCHAR(500)   DEFAULT NULL             COMMENT '目标客户群体，多个逗号分隔',
  target_customers_other VARCHAR(100) DEFAULT NULL         COMMENT '目标客户-其他说明',
  urgency         VARCHAR(100)    DEFAULT NULL             COMMENT '时间紧迫度',
  product_status  VARCHAR(100)    DEFAULT NULL             COMMENT '竞品情况',
  product_status_detail VARCHAR(200) DEFAULT NULL          COMMENT '竞品情况-哪家',
  -- 状态与时间
  status          VARCHAR(30)     DEFAULT NULL             COMMENT '状态: new/pending_supplement/initial_screening/judging/verifying/ipd_review/{stage}_rejected',
  submitted_at    DATETIME        DEFAULT NULL             COMMENT '提交时间',
  updated_at      DATETIME        DEFAULT NULL             COMMENT '更新时间',
  ipd_approved_at DATETIME        DEFAULT NULL             COMMENT 'IPD立项时间',
  -- 审计字段
  created_at      DATETIME        DEFAULT NULL             COMMENT '创建时间',
  created_by      VARCHAR(64)     DEFAULT NULL             COMMENT '创建人（飞连 user_id）',
  updated_by      VARCHAR(64)     DEFAULT NULL             COMMENT '更新人（飞连 user_id）',
  PRIMARY KEY (id),
  UNIQUE KEY uk_clue_no (clue_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='线索提报表';
```

## 2. 附件表 (attachment)

```sql
CREATE TABLE IF NOT EXISTS attachment (
  id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
  clue_id         BIGINT          DEFAULT NULL             COMMENT '关联线索ID',
  review_record_id BIGINT         DEFAULT NULL             COMMENT '关联审核记录ID',
  attach_type     VARCHAR(20)     DEFAULT NULL             COMMENT '附件类型: clue=线索附件 review=审核附件',
  original_name   VARCHAR(200)    DEFAULT NULL             COMMENT '原始文件名',
  stored_name     VARCHAR(200)    DEFAULT NULL             COMMENT '存储文件名（UUID）',
  file_path       VARCHAR(500)    DEFAULT NULL             COMMENT '文件路径',
  file_size       BIGINT          DEFAULT NULL             COMMENT '文件大小（字节）',
  uploaded_at     DATETIME        DEFAULT NULL             COMMENT '上传时间',
  -- 审计字段
  created_at      DATETIME        DEFAULT NULL             COMMENT '创建时间',
  created_by      VARCHAR(64)     DEFAULT NULL             COMMENT '创建人（飞连 user_id）',
  updated_at      DATETIME        DEFAULT NULL             COMMENT '更新时间',
  updated_by      VARCHAR(64)     DEFAULT NULL             COMMENT '更新人（飞连 user_id）',
  PRIMARY KEY (id),
  KEY idx_clue_attach (clue_id, attach_type),
  KEY idx_review_attach (review_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='附件表';
```

## 3. 审核记录表 (review_record)

```sql
CREATE TABLE IF NOT EXISTS review_record (
  id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
  clue_id         BIGINT          NOT NULL                 COMMENT '关联线索ID',
  review_stage    VARCHAR(30)     DEFAULT NULL             COMMENT '审核阶段: initial_screening/judging/verifying/ipd_review',
  action          VARCHAR(20)     DEFAULT NULL             COMMENT '操作: pass/reject/return',
  comment         TEXT                                     COMMENT '审核意见',
  reviewer_name   VARCHAR(50)     DEFAULT NULL             COMMENT '审核人姓名',
  reviewed_at     DATETIME        DEFAULT NULL             COMMENT '审核时间',
  -- 审计字段
  created_at      DATETIME        DEFAULT NULL             COMMENT '创建时间',
  created_by      VARCHAR(64)     DEFAULT NULL             COMMENT '创建人（飞连 user_id）',
  updated_at      DATETIME        DEFAULT NULL             COMMENT '更新时间',
  updated_by      VARCHAR(64)     DEFAULT NULL             COMMENT '更新人（飞连 user_id）',
  PRIMARY KEY (id),
  KEY idx_clue_id (clue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审核记录表';
```
