package com.cluehub.repository;

import com.cluehub.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /** 查询某条线索的附件 */
    List<Attachment> findByClueIdAndAttachType(Long clueId, String attachType);

    /** 查询某条审核记录的附件 */
    List<Attachment> findByReviewRecordId(Long reviewRecordId);
}
