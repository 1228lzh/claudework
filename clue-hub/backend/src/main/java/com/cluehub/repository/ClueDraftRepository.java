package com.cluehub.repository;

import com.cluehub.entity.ClueDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClueDraftRepository extends JpaRepository<ClueDraft, Long> {

    /** 根据企微userid查找用户的草稿 */
    Optional<ClueDraft> findByWecomUserId(String wecomUserId);
}
