package com.cluehub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理员白名单，从外部文件加载，运行时修改文件即可生效，无需重启
 */
@Slf4j
@Component
public class AdminListProvider {

    @Value("${app.admin-file:./admin-users.txt}")
    private String adminFilePath;

    private volatile Set<String> adminIds = Collections.emptySet();

    @PostConstruct
    public void init() {
        // 首次加载
        isAdmin("");
    }

    /**
     * 每次校验时调用，重新加载文件
     */
    public boolean isAdmin(String userId) {
        try {
            Path path = Paths.get(adminFilePath);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                Set<String> ids = new HashSet<>();
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        ids.add(trimmed);
                    }
                }
                this.adminIds = ids;
            }
        } catch (IOException e) {
            log.warn("Failed to read admin file: {}", adminFilePath, e);
        }
        return adminIds.contains(userId);
    }
}
