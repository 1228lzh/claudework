package com.cluehub.service;

import com.cluehub.dto.ClueSubmitDTO;
import com.cluehub.entity.Clue;
import com.cluehub.repository.ClueRepository;
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

    /**
     * 删除新建状态的线索（仅本人可删）
     */
    @Transactional
    public void deleteIfNew(Long id, String userId) {
        Clue clue = clueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("线索不存在"));
        if (!"new".equals(clue.getStatus())) {
            throw new RuntimeException("只能删除新建状态的线索");
        }
        if (!userId.equals(clue.getWecomUserId())) {
            throw new RuntimeException("无权删除此线索");
        }
        clueRepository.deleteById(id);
    }
}
