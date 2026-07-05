package com.cluehub.controller;

import com.cluehub.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;

/**
 * 文件下载控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 下载/预览附件
     */
    @GetMapping("/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
        try {
            FileService.AttachmentStream as = fileService.getStream(attachmentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename*=UTF-8''" +
                            URLEncoder.encode(as.name, "UTF-8"))
                    .body(new InputStreamResource(as.stream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
