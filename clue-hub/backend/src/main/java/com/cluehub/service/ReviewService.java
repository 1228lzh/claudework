package com.cluehub.service;

import com.cluehub.dto.ReviewDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ReviewRecord;
import com.cluehub.repository.ClueRepository;
import com.cluehub.repository.ReviewRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核服务：处理四阶段审核流转
 *
 * 状态体系：
 *   new - 新建（草稿/退回补充后）
 *   initial_screening - 初筛中
 *   judging - 研判中
 *   verifying - 验证中
 *   ipd_review - IPD立项
 *   approved - 已通过
 *   {stage}_rejected - 各阶段不通过
 *
 * 流转：
 *   new → 提交 → initial_screening
 *   initial_screening → pass → judging | reject → initial_screening_rejected | return → new
 *   judging → pass → verifying | reject → judging_rejected
 *   verifying → pass → ipd_review | reject → verifying_rejected
 *   ipd_review → pass → approved | reject → ipd_review_rejected
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ClueRepository clueRepository;
    private final ReviewRecordRepository reviewRecordRepository;

    /**
     * 处理审核操作
     */
    @Transactional
    public ReviewRecord review(Long clueId, ReviewDTO dto, String userId) {
        Clue clue = clueRepository.findById(clueId)
                .orElseThrow(() -> new RuntimeException("线索不存在"));

        String action = dto.getAction();
        String currentStatus = clue.getStatus();

        if ("return".equals(action)) {
            // 退回补充 → 待补充（仅初筛阶段可用）
            if ("initial_screening".equals(currentStatus)) {
                clue.setStatus("pending_supplement");
            } else {
                throw new RuntimeException("仅初筛阶段可以退回补充");
            }
        } else if ("reject".equals(action)) {
            // 不通过 → 当前阶段 + _rejected
            clue.setStatus(currentStatus + "_rejected");
        } else if ("pass".equals(action)) {
            // 通过 → 进入下一阶段
            String nextStage = getNextStage(currentStatus);
            clue.setStatus(nextStage);
            if ("ipd_review".equals(nextStage)) {
                if (dto.getIpdApprovedAt() != null && !dto.getIpdApprovedAt().isEmpty()) {
                    clue.setIpdApprovedAt(LocalDateTime.parse(dto.getIpdApprovedAt()));
                }
                if (dto.getCompletedAt() != null && !dto.getCompletedAt().isEmpty()) {
                    clue.setCompletedAt(LocalDateTime.parse(dto.getCompletedAt()));
                }
            }
        } else {
            throw new RuntimeException("未知操作：" + action);
        }

        clueRepository.save(clue);

        // 保存审核记录
        ReviewRecord record = ReviewRecord.builder()
                .clueId(clueId)
                .reviewStage(currentStatus)
                .action(action)
                .comment(dto.getComment())
                .reviewerName(dto.getReviewerName())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        return reviewRecordRepository.save(record);
    }

    /**
     * 获取审核记录
     */
    public List<ReviewRecord> getReviewHistory(Long clueId) {
        return reviewRecordRepository.findByClueIdOrderByReviewedAtDesc(clueId);
    }

    private String getNextStage(String currentStatus) {
        if ("initial_screening".equals(currentStatus)) return "judging";
        if ("judging".equals(currentStatus)) return "verifying";
        if ("verifying".equals(currentStatus)) return "ipd_review";
        throw new RuntimeException("无法确定下一阶段：" + currentStatus);
    }
}
