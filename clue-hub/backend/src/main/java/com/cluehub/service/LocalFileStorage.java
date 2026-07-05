package com.cluehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 本地文件系统存储实现
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.storage-type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    @Value("${app.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public String store(InputStream inputStream, String originalName, String contentType, String attachType) throws IOException {
        Path uploadDir = Paths.get(uploadPath, attachType);
        Files.createDirectories(uploadDir);

        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path targetPath = uploadDir.resolve(storedName);

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.debug("File stored: {}", targetPath);

        return targetPath.toString();
    }

    @Override
    public InputStream load(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
    }

    @Override
    public void delete(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
        log.debug("File deleted: {}", path);
    }
}
