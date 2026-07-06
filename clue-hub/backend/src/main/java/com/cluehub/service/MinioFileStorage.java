package com.cluehub.service;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO 对象存储实现
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.storage-type", havingValue = "minio")
public class MinioFileStorage implements FileStorage {

    @Value("${app.minio.endpoint}")
    private String endpoint;

    @Value("${app.minio.access-key}")
    private String accessKey;

    @Value("${app.minio.secret-key}")
    private String secretKey;

    @Value("${app.minio.bucket}")
    private String bucket;

    private MinioClient client;

    @PostConstruct
    public void init() {
        client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        ensureBucket();
    }

    private void ensureBucket() {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("MinIO bucket created: {}", bucket);
            }
        } catch (Exception e) {
            log.error("Failed to ensure MinIO bucket: {}", bucket, e);
            throw new RuntimeException("MinIO bucket init failed", e);
        }
    }

    @Override
    public String store(InputStream inputStream, String originalName, String contentType, String attachType) throws Exception {
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String objectName = "clueHub/" + attachType + "/" + UUID.randomUUID().toString() + ext;

        client.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(inputStream, -1, 10485760) // 10MB part size
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build());

        log.debug("File stored in MinIO: {}/{}", bucket, objectName);
        return objectName;
    }

    @Override
    public InputStream load(String path) throws Exception {
        return client.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());
    }

    @Override
    public void delete(String path) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build());
        log.debug("File deleted from MinIO: {}/{}", bucket, path);
    }
}
