package com.cluehub.service;

import com.cluehub.entity.Attachment;
import com.cluehub.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 上传附件（线索附件或审核附件）
     */
    public Attachment upload(MultipartFile file, Long clueId, Long reviewRecordId, String attachType, String userId) throws IOException {
        Path uploadDir = Paths.get(uploadPath, attachType);
        Files.createDirectories(uploadDir);

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;

        Path targetPath = uploadDir.resolve(storedName);
        file.transferTo(targetPath.toFile());

        Attachment attachment = Attachment.builder()
                .clueId(clueId)
                .reviewRecordId(reviewRecordId)
                .attachType(attachType)
                .originalName(originalName)
                .storedName(storedName)
                .filePath(targetPath.toString())
                .fileSize(file.getSize())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        return attachmentRepository.save(attachment);
    }

    /**
     * 查询线索的附件列表
     */
    public List<Attachment> getClueAttachments(Long clueId) {
        return attachmentRepository.findByClueIdAndAttachType(clueId, "clue");
    }

    /**
     * 查询审核记录的附件列表
     */
    public List<Attachment> getReviewAttachments(Long reviewRecordId) {
        return attachmentRepository.findByReviewRecordId(reviewRecordId);
    }

    /**
     * 获取附件文件路径
     */
    public Path getAttachmentFile(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("附件不存在"));
        return Paths.get(attachment.getFilePath());
    }
}
