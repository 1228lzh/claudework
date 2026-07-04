package com.cluehub.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 附件
 */
@Entity
@Table(name = "attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联线索ID */
    private Long clueId;

    /** 关联审核记录ID（审核附件时使用） */
    private Long reviewRecordId;

    /** 附件类型：clue=线索附件, review=审核附件 */
    @Column(length = 20)
    private String attachType;

    /** 原始文件名 */
    @Column(length = 200)
    private String originalName;

    /** 存储文件名 */
    @Column(length = 200)
    private String storedName;

    /** 文件路径 */
    @Column(length = 500)
    private String filePath;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 上传时间 */
    private LocalDateTime uploadedAt;

    // ===== 审计字段 =====
    private LocalDateTime createdAt;
    @Column(length = 64)
    private String createdBy;
    private LocalDateTime updatedAt;
    @Column(length = 64)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
