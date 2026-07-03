package com.cluehub.repository;

import com.cluehub.entity.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRecordRepository extends JpaRepository<ReviewRecord, Long> {

    /** 查询某条线索的所有审核记录，按时间排序 */
    List<ReviewRecord> findByClueIdOrderByReviewedAtDesc(Long clueId);
}
