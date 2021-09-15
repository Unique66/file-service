package com.file.service.util;

import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.StringUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Unique66
 * @description 文件压缩
 * @date 2021/9/15 21:23
 */
public class ZipUtils {
    //
    public static void main(String[] args) {
        File fileDir = new File("F:\\test\\picture\\split");


        resultZipCompress("F:\\test\\picture\\test.zip", fileDir.listFiles());
    }

    // 指定目录压缩
    public static void resultZipCompress(String zipPath, File[] fileList) {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath))) {
            compress(out, fileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void compress(ZipOutputStream out, File[] fileList) throws IOException {
        if (ArrayUtils.isEmpty(fileList)) {
            return;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    compress(out, files);
                }
            } else {
                // 是文件，分为三种情况
                if (StringUtils.equals("test_0.mp4", file.getName())
                        || StringUtils.equals("test_1.mp4", file.getName())) {
                    out.putNextEntry(new ZipEntry("Attachment" + File.separator + file.getName()));
                } else if (StringUtils.equals("test_2.mp4", file.getName())
                        || StringUtils.equals("test_3.mp4", file.getName())) {
                    out.putNextEntry(new ZipEntry("Data" + File.separator + file.getName()));
                } else {
                    out.putNextEntry(new ZipEntry("Panorama" + File.separator + file.getName()));
                }
                try (FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis)) {
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        }
    }
}
