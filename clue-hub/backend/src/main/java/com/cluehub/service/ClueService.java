package com.cluehub.service;

import com.cluehub.dto.ClueSubmitDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ClueDraft;
import com.cluehub.repository.ClueRepository;
import com.cluehub.repository.ClueDraftRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClueService {

    private final ClueRepository clueRepository;
    private final ClueDraftRepository draftRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 生成线索编号
     */
    private String generateClueNo() {
        String today = LocalDateTime.now().format(NO_FORMAT);
        long count = clueRepository.count();
        return String.format("CL%s%04d", today, (count + 1) % 10000);
    }

    /**
     * 提交线索 → 进入初筛中
     * 如果是暂存线索提交，直接更新状态；否则新建
     */
    @Transactional
    public Clue submit(ClueSubmitDTO dto) {
        Clue clue;
        if (dto.getDraftId() != null) {
            // 暂存线索提交：更新现有记录
            clue = clueRepository.findById(dto.getDraftId())
                    .orElseThrow(() -> new RuntimeException("线索不存在"));
            if (clue.getClueNo() == null) {
                clue.setClueNo(generateClueNo());
            }
        } else {
            // 全新提交
            clue = new Clue();
            clue.setClueNo(generateClueNo());
        }

        fillClueFields(clue, dto);
        clue.setStatus("initial_screening");
        clue.setSubmittedAt(LocalDateTime.now());

        return clueRepository.save(clue);
    }

    /**
     * 保存草稿（存草稿表，不影响线索表）
     */
    @Transactional
    public ClueDraft saveDraft(ClueSubmitDTO dto) throws JsonProcessingException {
        String draftJson = objectMapper.writeValueAsString(dto);
        ClueDraft draft;
        if (dto.getDraftId() != null) {
            draft = draftRepository.findById(dto.getDraftId()).orElse(new ClueDraft());
        } else if (dto.getWecomUserId() != null) {
            draft = draftRepository.findByWecomUserId(dto.getWecomUserId()).orElse(new ClueDraft());
        } else {
            draft = new ClueDraft();
        }
        draft.setWecomUserId(dto.getWecomUserId());
        draft.setDraftData(draftJson);
        draft.setCurrentStep(dto.getAction() != null ? extractStep(dto) : 1);
        return draftRepository.save(draft);
    }

    private int extractStep(ClueSubmitDTO dto) {
        if (dto.getAction() != null && dto.getAction().startsWith("draft_step_")) {
            try {
                return Integer.parseInt(dto.getAction().substring("draft_step_".length()));
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    public Optional<ClueDraft> getDraft(String wecomUserId) {
        return draftRepository.findByWecomUserId(wecomUserId);
    }

    public Optional<Clue> getById(Long id) {
        return clueRepository.findById(id);
    }

    /** 我的线索（包含新建/暂存状态） */
    public List<Clue> getUserClues(String wecomUserId) {
        return clueRepository.findByWecomUserIdOrderByUpdatedAtDesc(wecomUserId);
    }

    /** 管理端：所有非新建线索 */
    public List<Clue> getAllSubmitted() {
        return clueRepository.findAllNonNew();
    }

    /** 管理端：按状态筛选 */
    public List<Clue> getByStatuses(List<String> statuses) {
        return clueRepository.findByStatusInOrderBySubmittedAtDesc(statuses);
    }

    /** 暂存线索 → 状态=new。有draftId则更新同一条，没draftId则新建 */
    @Transactional
    public Clue saveNew(ClueSubmitDTO dto) {
        Clue clue;
        if (dto.getDraftId() != null) {
            // 继续编辑已有的暂存线索 → 更新
            clue = clueRepository.findById(dto.getDraftId())
                    .orElseThrow(() -> new RuntimeException("线索不存在"));
        } else {
            // 全新暂存 → 新建一条
            clue = new Clue();
            clue.setClueNo(generateClueNo());
        }

        fillClueFields(clue, dto);
        clue.setStatus("new");
        return clueRepository.save(clue);
    }

    /** 获取用户暂存的线索 */
    public Optional<Clue> getPending(String wecomUserId) {
        return clueRepository.findByWecomUserIdAndStatus(wecomUserId, "new");
    }

    private void fillClueFields(Clue clue, ClueSubmitDTO dto) {
        clue.setReporterName(dto.getReporterName());
        clue.setReporterDept(dto.getReporterDept());
        clue.setReporterContact(dto.getReporterContact());
        clue.setWecomUserId(dto.getWecomUserId());
        clue.setClueName(dto.getClueName());
        clue.setClueType(dto.getClueType());
        clue.setClueTypeOther(dto.getClueTypeOther());
        clue.setClueDesc(dto.getClueDesc());
        clue.setInfoSource(dto.getInfoSource());
        clue.setInfoSourceOther(dto.getInfoSourceOther());
        clue.setReliability(dto.getReliability());
        clue.setMarketSize(dto.getMarketSize());
        clue.setProductLines(dto.getProductLines());
        clue.setProductLinesDetail(dto.getProductLinesDetail());
        clue.setTargetCustomers(dto.getTargetCustomers());
        clue.setTargetCustomersOther(dto.getTargetCustomersOther());
        clue.setUrgency(dto.getUrgency());
        clue.setProductStatus(dto.getProductStatus());
        clue.setProductStatusDetail(dto.getProductStatusDetail());
    }

    /** 管理端：活跃审核中的线索 */
    public List<Clue> getActiveReview() {
        return clueRepository.findActiveReview();
    }

    /**
     * 退回补充后重新提交 → 再次进入初筛中
     */
    @Transactional
    public Clue update(Long id, ClueSubmitDTO dto) {
        Clue clue = clueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("线索不存在"));
        clue.setClueName(dto.getClueName());
        clue.setClueType(dto.getClueType());
        clue.setClueTypeOther(dto.getClueTypeOther());
        clue.setClueDesc(dto.getClueDesc());
        clue.setInfoSource(dto.getInfoSource());
        clue.setInfoSourceOther(dto.getInfoSourceOther());
        clue.setReliability(dto.getReliability());
        clue.setMarketSize(dto.getMarketSize());
        clue.setProductLines(dto.getProductLines());
        clue.setProductLinesDetail(dto.getProductLinesDetail());
        clue.setTargetCustomers(dto.getTargetCustomers());
        clue.setTargetCustomersOther(dto.getTargetCustomersOther());
        clue.setUrgency(dto.getUrgency());
        clue.setProductStatus(dto.getProductStatus());
        clue.setProductStatusDetail(dto.getProductStatusDetail());
        clue.setReporterName(dto.getReporterName());
        clue.setReporterDept(dto.getReporterDept());
        clue.setReporterContact(dto.getReporterContact());
        clue.setStatus("initial_screening");
        clue.setSubmittedAt(LocalDateTime.now());
        return clueRepository.save(clue);
    }
}
