package com.file.service.controller;

import com.file.service.model.vo.BreakPointVO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;

/**
 * @author Unique66
 * @description 文件上传下载
 * @date 2021/8/22 17:50
 */
@RestController
public class FileController {
    static String PATH = "F:/test/testUpload/";

    // localhost:8090/file-service/test
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    // localhost:8090/file-service/test1?param=who i am
    @GetMapping("/test1")
    public String test1(@RequestParam(value = "param") String param) {
        return "return " + param;
    }

    @PostMapping("/uploadFile")
    public String upload(MultipartFile file, String fileName) throws IOException {
        InputStream is = file.getInputStream();
        byte[] bytes = new byte[1024];
        RandomAccessFile os = new RandomAccessFile(PATH + fileName,"rw");
        while (is.read(bytes) != -1) {
            os.write(bytes);
        }
        os.close();
        is.close();
        return fileName;
    }

    // 分片上传
    @GetMapping("/download")
    public void chunkUploadFile(@RequestParam("fileName") String fileName,
        HttpServletRequest request, HttpServletResponse response) {
//        String path = "F:/test/testDownload/";
        String path = "E:\\迅雷下载\\哈林波特\\";
        File downloadFile = new File(path + fileName);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, downloadFile.length() + "");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="
                + new String(fileName.getBytes()));
        response.setContentType("application/octet-stream");

        byte[] bytes = new byte[1024 * 1024];
        try (RandomAccessFile is = new RandomAccessFile(downloadFile,"rw");
            OutputStream os = response.getOutputStream()) {
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            e.printStackTrace();
        }
    }

    static String BREAK_POINT_PATH = PATH + "breakPoint/";
    // 断点续传
    @PostMapping("/breakPointResumeFile")
    public String breakPointResumeFile(BreakPointVO breakPointVO) throws IOException {
        // static String PATH = "F:/test/testUpload/";
        if (PROGRESS[breakPointVO.getChunk()] == 1) {
            return "chunk" + breakPointVO.getChunk() + "had been upload.";
        }
        ///1、获取目标路径
        String targetPath;
        if (breakPointVO.getChunk() == 0) { // 第一片，要存储redis
//            targetPath = PATH + UUID.randomUUID().toString();
            targetPath = BREAK_POINT_PATH;
            boolean redisRecord = redisRecord(breakPointVO, targetPath);
            if (!redisRecord) {
                return "redisRecord fail.";
            }
        } else {
            targetPath = getTargetPathFromRedis(breakPointVO);
        }
        // 2、存储分片文件
        saveChunkFile(breakPointVO, targetPath);
        // 3、记录文件续传进度
        boolean over = checkAndSetUploadProgress(breakPointVO);
        if (over) {
            return BREAK_POINT_PATH;
        }
        return "ok";
    }

    // 获取该上传哪一片
    // localhost:8090/file-service/getCurrentChunk
    @PostMapping("/getCurrentChunk")
    public int getCurrentChunk(String md5) {
        for (int i = 0; i < PROGRESS.length; i++) {
            if (PROGRESS[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean redisRecord(BreakPointVO breakPointVO, String targetPath) {
        // redis.save(breakPointVO.getHash(), targetPath);
        // 直接save，如果是false，说明已经传过第一片，不用操作。
        // 如果是true，则继续下面的存储分片和分片进度操作
        return true;
    }

    ///1、获取目标路径
    private String getTargetPathFromRedis(BreakPointVO breakPointVO) {
        // redis.get(breakPointVO.getHash());
        return BREAK_POINT_PATH;
    }

    // 2、存储分片文件
    private void saveChunkFile(BreakPointVO breakPointVO, String targetPath) {
        String targetFilePath = targetPath + breakPointVO.getName();
        try (RandomAccessFile out = new RandomAccessFile(targetFilePath, "rw")){
            out.seek(breakPointVO.getChunk() * breakPointVO.getChunkSize());
            out.write(breakPointVO.getFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int[] PROGRESS = new int[11];
    // 3、记录文件续传进度
    private boolean checkAndSetUploadProgress(BreakPointVO breakPointVO) {
        PROGRESS[breakPointVO.getChunk()] = 1; // 记录进度

        // 判定是否所有都为1
        boolean over = true;
        for (int pro : PROGRESS) {
            if (pro == 0) {
                over = false;
                break;
            }
        }
        System.out.println("progress:" + Arrays.toString(PROGRESS));
        return over;
    }
}
