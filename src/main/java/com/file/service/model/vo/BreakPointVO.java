package com.file.service.model.vo;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Unique66
 * @description 断点续传入参
 * @date 2021/8/29 21:28
 */
public class BreakPointVO {
    private String name; // 文件名
    private String hash; // 文件md5 值
    private int chunk; // 第几片
    private int chunks; // 文件总分片数
    private long chunkSize; // 分片大小
    private long size; // 文件总大小
    private MultipartFile file; // 切片文件

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
