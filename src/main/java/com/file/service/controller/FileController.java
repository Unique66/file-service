package com.file.service.controller;

import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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
    @PostMapping("/chunkUploadFile")
    public String chunkUploadFile(MultipartFile file, String fileName) throws IOException {
        String path = "F:/test/testUpload/";
        InputStream is = file.getInputStream();
        byte[] bytes = new byte[1024];
        RandomAccessFile os = new RandomAccessFile(path + fileName,"rw");
        while (is.read(bytes) != -1) {
            os.write(bytes);
        }
        String fileMD5 = DigestUtils.md5DigestAsHex(file.getInputStream());
        System.out.println(fileMD5);
        System.out.println(DigestUtils.md5DigestAsHex(
                new FileInputStream(new File(path + fileName))));
        System.out.println(DigestUtils.md5DigestAsHex(
                new FileInputStream(new File(path + fileName))));
        System.out.println(fileMD5.equals(DigestUtils.md5DigestAsHex(
                new FileInputStream(new File(path + fileName)))));
        os.close();
        is.close();
        return fileName;
    }
}
