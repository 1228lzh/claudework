package com.cluehub.controller;

import com.cluehub.dto.ApiResponse;
import com.cluehub.dto.ReviewDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ReviewRecord;
import com.cluehub.service.ClueService;
import com.cluehub.service.FileService;
import com.cluehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 审核控制器
 */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ClueService clueService;
    private final FileService fileService;

    /**
     * 管理端：获取所有已提交的线索列表
     */
    @GetMapping("/clues")
    public ApiResponse<List<Clue>> listClues(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            List<String> statuses = Arrays.asList(status.split(","));
            return ApiResponse.ok(clueService.getByStatuses(statuses));
        }
        return ApiResponse.ok(clueService.getAllSubmitted());
    }

    /**
     * 审核操作
     */
    @PostMapping("/{clueId}")
    public ApiResponse<ReviewRecord> review(@PathVariable Long clueId, @RequestBody ReviewDTO dto) {
        try {
            ReviewRecord record = reviewService.review(clueId, dto);
            return ApiResponse.ok(record);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 获取审核历史
     */
    @GetMapping("/{clueId}/history")
    public ApiResponse<List<ReviewRecord>> history(@PathVariable Long clueId) {
        return ApiResponse.ok(reviewService.getReviewHistory(clueId));
    }

    /**
     * 审核附件上传
     */
    @PostMapping("/{reviewRecordId}/upload")
    public ApiResponse<?> uploadReviewAttachment(@PathVariable Long reviewRecordId,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            return ApiResponse.ok(fileService.upload(file, null, reviewRecordId, "review"));
        } catch (Exception e) {
            return ApiResponse.fail("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取审核附件列表
     */
    @GetMapping("/{reviewRecordId}/attachments")
    public ApiResponse<?> getReviewAttachments(@PathVariable Long reviewRecordId) {
        return ApiResponse.ok(fileService.getReviewAttachments(reviewRecordId));
    }
}
