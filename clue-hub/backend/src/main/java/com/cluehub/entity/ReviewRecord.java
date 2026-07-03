package com.cluehub.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 审核记录
 */
@Entity
@Table(name = "review_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联线索ID */
    @Column(nullable = false)
    private Long clueId;

    /** 审核阶段：initial_screening/judging/verifying/ipd_review */
    @Column(length = 30)
    private String reviewStage;

    /** 操作：pass/reject/return */
    @Column(length = 20)
    private String action;

    /** 审核意见 */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** 审核人姓名 */
    @Column(length = 50)
    private String reviewerName;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        if (reviewedAt == null) {
            reviewedAt = LocalDateTime.now();
        }
    }
}
