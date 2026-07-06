package com.cluehub.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 线索提报表
 */
@Entity
@Table(name = "clue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 线索编号，如 CL202607020001 */
    @Column(unique = true, length = 20)
    private String clueNo;

    // ===== 第一步：谁报的 =====
    /** 报备人（企微自动带出） */
    @Column(length = 50)
    private String reporterName;

    /** 部门/单位 */
    @Column(length = 100)
    private String reporterDept;

    /** 联系方式 */
    @Column(length = 50)
    private String reporterContact;

    /** 企微 userid */
    @Column(length = 64)
    private String wecomUserId;

    // ===== 第二步：什么线索 =====
    /** 线索名称 */
    @Column(length = 100)
    private String clueName;

    /** 线索类型 */
    @Column(length = 100)
    private String clueType;

    /** 线索类型-其他说明 */
    @Column(length = 100)
    private String clueTypeOther;

    /** 线索描述（3-5句话） */
    @Column(columnDefinition = "TEXT")
    private String clueDesc;

    // ===== 第三步：线索来源 =====
    /** 信息来源，多个用逗号分隔 */
    @Column(length = 500)
    private String infoSource;

    /** 信息来源-其他说明 */
    @Column(length = 100)
    private String infoSourceOther;

    /** 信息可靠度：高/中/低 */
    @Column(length = 100)
    private String reliability;

    /** 预计市场规模 */
    @Column(length = 200)
    private String marketSize;

    // ===== 第四步：线索判断 =====
    /** 涉及品类/产品线，多个用逗号分隔 */
    @Column(length = 500)
    private String productLines;

    /** 全品类-具体说明 */
    @Column(length = 100)
    private String productLinesDetail;

    /** 目标客户群体，多个用逗号分隔 */
    @Column(length = 500)
    private String targetCustomers;

    /** 目标客户-其他说明 */
    @Column(length = 100)
    private String targetCustomersOther;

    /** 时间紧迫度 */
    @Column(length = 100)
    private String urgency;

    /** 竞品情况：竞品已经在做了/竞品还没做/市场上还没有人做/不清楚 */
    @Column(length = 100)
    private String productStatus;

    /** 竞品情况-哪家 */
    @Column(length = 200)
    private String productStatusDetail;

    // ===== 状态与审核 =====
    /**
     * 状态：
     * new - 新建（草稿 / 退回补充后）
     * initial_screening - 初筛中
     * judging - 研判中
     * verifying - 验证中
     * ipd_review - IPD立项（终态）
     * initial_screening_rejected - 初筛不通过
     * judging_rejected - 研判不通过
     * verifying_rejected - 验证不通过
     */
    @Column(length = 30)
    private String status;

    /** 提交时间 */
    private LocalDateTime submittedAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** IPD立项时间 */
    private LocalDateTime ipdApprovedAt;

    /** 完结时间 */
    private LocalDateTime completedAt;

    // ===== 审计字段 =====
    @Column(length = 64)
    private String createdBy;
    @Column(length = 64)
    private String updatedBy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (submittedAt == null && !"draft".equals(status)) {
            submittedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
