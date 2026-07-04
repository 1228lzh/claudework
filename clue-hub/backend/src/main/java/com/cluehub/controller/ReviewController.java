package com.cluehub.controller;

import com.cluehub.dto.ApiResponse;
import com.cluehub.dto.ReviewDTO;
import com.cluehub.entity.Clue;
import com.cluehub.entity.ReviewRecord;
import com.cluehub.model.UserInfo;
import com.cluehub.service.ClueService;
import com.cluehub.service.FileService;
import com.cluehub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ClueService clueService;
    private final FileService fileService;

    @GetMapping("/clues")
    public ApiResponse<List<Clue>> listClues(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            List<String> statuses = Arrays.asList(status.split(","));
            return ApiResponse.ok(clueService.getByStatuses(statuses));
        }
        return ApiResponse.ok(clueService.getAllSubmitted());
    }

    @PostMapping("/{clueId}")
    public ApiResponse<ReviewRecord> review(@PathVariable Long clueId,
                                             @RequestBody ReviewDTO dto,
                                             HttpServletRequest request) {
        try {
            UserInfo user = (UserInfo) request.getAttribute("currentUser");
            dto.setReviewerName(user != null ? user.getFullname() : "审核员");
            String userId = user != null ? user.getUserId() : null;
            ReviewRecord record = reviewService.review(clueId, dto, userId);
            return ApiResponse.ok(record);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/{clueId}/history")
    public ApiResponse<List<ReviewRecord>> history(@PathVariable Long clueId) {
        return ApiResponse.ok(reviewService.getReviewHistory(clueId));
    }

    @PostMapping("/{reviewRecordId}/upload")
    public ApiResponse<?> uploadReviewAttachment(@PathVariable Long reviewRecordId,
                                                  @RequestParam("file") MultipartFile file,
                                                  HttpServletRequest request) {
        try {
            UserInfo user = (UserInfo) request.getAttribute("currentUser");
            String userId = user != null ? user.getUserId() : null;
            return ApiResponse.ok(fileService.upload(file, null, reviewRecordId, "review", userId));
        } catch (Exception e) {
            return ApiResponse.fail("上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/{reviewRecordId}/attachments")
    public ApiResponse<?> getReviewAttachments(@PathVariable Long reviewRecordId) {
        return ApiResponse.ok(fileService.getReviewAttachments(reviewRecordId));
    }
}
