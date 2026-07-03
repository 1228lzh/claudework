package com.cluehub.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 草稿表
 */
@Entity
@Table(name = "clue_draft")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClueDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 企微 userid，用于关联用户 */
    @Column(length = 64)
    private String wecomUserId;

    /** 当前步骤：1-5 */
    private Integer currentStep;

    /** 草稿数据，JSON存储 */
    @Column(columnDefinition = "MEDIUMTEXT")
    private String draftData;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
