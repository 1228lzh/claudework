package com.cluehub.controller;

import com.cluehub.dto.ApiResponse;
import com.cluehub.dto.ClueSubmitDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ClueDraft;
import com.cluehub.model.UserInfo;
import com.cluehub.service.ClueService;
import com.cluehub.service.FileService;
import javax.servlet.http.HttpServletRequest;
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

    private String getUserId(HttpServletRequest request) {
        UserInfo user = (UserInfo) request.getAttribute("currentUser");
        return user != null ? user.getUserId() : null;
    }

    /** 提交线索 → 进入初筛中 */
    @PostMapping("/submit")
    public ApiResponse<Clue> submit(@Valid @RequestBody ClueSubmitDTO dto,
                                     HttpServletRequest request) {
        dto.setWecomUserId(getUserId(request));
        Clue clue = clueService.submit(dto);
        return ApiResponse.ok("提交成功", clue);
    }

    /** 暂存线索 → 状态为新建 */
    @PostMapping("/save")
    public ApiResponse<Clue> save(@RequestBody ClueSubmitDTO dto,
                                   HttpServletRequest request) {
        dto.setWecomUserId(getUserId(request));
        Clue clue = clueService.saveNew(dto);
        return ApiResponse.ok("暂存成功", clue);
    }

    /** 获取草稿 */
    @GetMapping("/draft")
    public ApiResponse<ClueDraft> getDraft(HttpServletRequest request) {
        Optional<ClueDraft> draft = clueService.getDraft(getUserId(request));
        return draft.map(ApiResponse::ok).orElse(ApiResponse.ok(null));
    }

    /** 获取用户暂存的线索 */
    @GetMapping("/pending")
    public ApiResponse<Clue> getPending(HttpServletRequest request) {
        return clueService.getPending(getUserId(request))
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
    public ApiResponse<List<Clue>> myClues(HttpServletRequest request) {
        return ApiResponse.ok(clueService.getUserClues(getUserId(request)));
    }

    /** 重新提交 */
    @PutMapping("/{id}/resubmit")
    public ApiResponse<Clue> resubmit(@PathVariable Long id,
                                       @Valid @RequestBody ClueSubmitDTO dto,
                                       HttpServletRequest request) {
        dto.setWecomUserId(getUserId(request));
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
