package com.cluehub.repository;

import com.cluehub.entity.Clue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ClueRepository extends JpaRepository<Clue, Long> {

    Optional<Clue> findByClueNo(String clueNo);

    /** 根据企微userid查询用户的所有线索（含新建状态） */
    List<Clue> findByWecomUserIdOrderByUpdatedAtDesc(String wecomUserId);

    /** 查询用户暂存的线索（状态=new） */
    Optional<Clue> findByWecomUserIdAndStatus(String wecomUserId, String status);

    /** 查询所有非新建的线索，按阶段筛选 */
    List<Clue> findByStatusInOrderBySubmittedAtDesc(List<String> statuses);

    /** 查询所有非新建线索 */
    @Query("SELECT c FROM Clue c WHERE c.status != 'new' ORDER BY c.submittedAt DESC")
    List<Clue> findAllSubmitted();

    /** 查询活跃审核中的线索（初筛中/研判中/验证中/IPD立项） */
    @Query("SELECT c FROM Clue c WHERE c.status IN ('initial_screening','judging','verifying','ipd_review') ORDER BY c.submittedAt DESC")
    List<Clue> findActiveReview();

    /** 查询所有非新建的线索（管理端用） */
    @Query("SELECT c FROM Clue c WHERE c.status != 'new' ORDER BY c.submittedAt DESC")
    List<Clue> findAllNonNew();
}
