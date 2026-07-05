package com.cluehub.service;

import java.io.InputStream;

/**
 * 文件存储抽象接口：统一本地文件系统和 MinIO 的调用方式
 */
public interface FileStorage {

    /**
     * 存储文件
     * @param inputStream 文件流
     * @param originalName 原始文件名
     * @param contentType 文件类型
     * @param attachType 附件类型（clue/review）
     * @return 存储路径或对象键
     */
    String store(InputStream inputStream, String originalName, String contentType, String attachType) throws Exception;

    /**
     * 读取文件
     * @param path 存储路径（本地路径或 MinIO 对象键）
     * @return 文件流
     */
    InputStream load(String path) throws Exception;

    /**
     * 删除文件
     * @param path 存储路径
     */
    void delete(String path) throws Exception;
}
