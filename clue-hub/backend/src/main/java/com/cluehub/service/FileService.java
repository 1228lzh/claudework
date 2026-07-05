package com.cluehub.service;

import com.cluehub.entity.Attachment;
import com.cluehub.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorage fileStorage;

    /**
     * 上传附件
     */
    public Attachment upload(MultipartFile file, Long clueId, Long reviewRecordId, String attachType, String userId) throws Exception {
        String path = fileStorage.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                attachType);

        Attachment attachment = Attachment.builder()
                .clueId(clueId)
                .reviewRecordId(reviewRecordId)
                .attachType(attachType)
                .originalName(file.getOriginalFilename())
                .storedName(path)
                .filePath(path)
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
     * 根据附件 ID 获取附件元数据
     */
    public Attachment getById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("附件不存在"));
    }

    /**
     * 读取附件文件流
     */
    public AttachmentStream getStream(Long attachmentId) throws Exception {
        Attachment attachment = getById(attachmentId);
        return new AttachmentStream(
                fileStorage.load(attachment.getFilePath()),
                attachment.getOriginalName());
    }

    /**
     * 删除附件
     */
    public void deleteAttachment(Long attachmentId) throws Exception {
        Attachment attachment = getById(attachmentId);
        fileStorage.delete(attachment.getFilePath());
        attachmentRepository.deleteById(attachmentId);
    }

    public static class AttachmentStream {
        public final java.io.InputStream stream;
        public final String name;

        public AttachmentStream(java.io.InputStream stream, String name) {
            this.stream = stream;
            this.name = name;
        }
    }
}
