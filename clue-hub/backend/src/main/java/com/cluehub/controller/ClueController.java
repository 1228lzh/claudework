package com.cluehub.controller;

import com.cluehub.dto.ApiResponse;
import com.cluehub.dto.ClueSubmitDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ClueDraft;
import com.cluehub.service.ClueService;
import com.cluehub.service.FileService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clue")
@RequiredArgsConstructor
public class ClueController {

    private final ClueService clueService;
    private final FileService fileService;

    /** 提交线索 → 进入初筛中 */
    @PostMapping("/submit")
    public ApiResponse<Clue> submit(@Valid @RequestBody ClueSubmitDTO dto) {
        Clue clue = clueService.submit(dto);
        return ApiResponse.ok("提交成功", clue);
    }

    /** 暂存线索 → 状态为新建（不做必填校验，允许分步填写） */
    @PostMapping("/save")
    public ApiResponse<Clue> save(@RequestBody ClueSubmitDTO dto) {
        Clue clue = clueService.saveNew(dto);
        return ApiResponse.ok("暂存成功", clue);
    }

    /** 获取草稿（旧草稿表） */
    @GetMapping("/draft")
    public ApiResponse<ClueDraft> getDraft(@RequestParam String wecomUserId) {
        Optional<ClueDraft> draft = clueService.getDraft(wecomUserId);
        return draft.map(ApiResponse::ok).orElse(ApiResponse.ok(null));
    }

    /** 获取用户暂存的线索（状态=new） */
    @GetMapping("/pending")
    public ApiResponse<Clue> getPending(@RequestParam String wecomUserId) {
        return clueService.getPending(wecomUserId)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.ok(null));
    }

    /** 线索详情 */
    @GetMapping("/{id}")
    public ApiResponse<Clue> detail(@PathVariable Long id) {
        return clueService.getById(id)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("线索不存在"));
    }

    /** 我的线索列表 */
    @GetMapping("/my")
    public ApiResponse<List<Clue>> myClues(@RequestParam String wecomUserId) {
        return ApiResponse.ok(clueService.getUserClues(wecomUserId));
    }

    /** 重新提交（退回补充后） */
    @PutMapping("/{id}/resubmit")
    public ApiResponse<Clue> resubmit(@PathVariable Long id, @Valid @RequestBody ClueSubmitDTO dto) {
        Clue clue = clueService.update(id, dto);
        return ApiResponse.ok("重新提交成功", clue);
    }

    /** 上传附件 */
    @PostMapping("/{id}/upload")
    public ApiResponse<?> uploadAttachment(@PathVariable Long id,
                                           @RequestParam("file") MultipartFile file) {
        try {
            return ApiResponse.ok(fileService.upload(file, id, null, "clue"));
        } catch (Exception e) {
            return ApiResponse.fail("上传失败：" + e.getMessage());
        }
    }

    /** 获取附件列表 */
    @GetMapping("/{id}/attachments")
    public ApiResponse<?> getAttachments(@PathVariable Long id) {
        return ApiResponse.ok(fileService.getClueAttachments(id));
    }
}
