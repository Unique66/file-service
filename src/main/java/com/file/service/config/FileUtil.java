package com.file.service.config;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Unique66
 * @description 文件工具，测试分片和合并
 * @date 2021/8/29 19:59
 */
public class FileUtil {
    private static int CHUNK_SIZE = 10; // 以10MB 每份
    private static String PATH = "F:/test/picture/";

    public static void main(String[] args) {
        // 1、分片文件
        List<String> split = split(PATH + "test.mp4");
        System.out.println(split.toString());
        // 合并文件
        merge(split);
    }

    public static void merge(List<String> split)  {
        String targetFilePath = PATH + "merge" + split.get(0).substring(split.get(0).lastIndexOf("."));
//        for (int chunk = 0; chunk < 5; chunk++) {
        for (int chunk = 10; chunk < 11; chunk++) {
//        for (int chunk = 0; chunk < split.size(); chunk++) {
            try (RandomAccessFile input = new RandomAccessFile(split.get(chunk), "r");
                RandomAccessFile out = new RandomAccessFile(targetFilePath, "rw")){
                out.seek(chunk * CHUNK_SIZE * 1024 * 1024);
                byte[] bytes = new byte[1024];
                while (input.read(bytes) != -1) {
                    out.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<String> split(String path) {
        File toSplitFile = new File(path);
        long size = toSplitFile.length() / 1024; // kb
        System.out.println("size:" + size + "KB");
        System.out.println("size:" + size / 1024 + "MB");
        long chunks = (size / 1024 / CHUNK_SIZE) + ((size / 1024 % CHUNK_SIZE) == 0 ? 0 : 1);
        System.out.println("chunks:" + chunks);
        List<String> splitFileList = new ArrayList<>((int) chunks);
        for (int i = 0; i < chunks; i++) {
            splitFile(i, toSplitFile, splitFileList);
        }
        return splitFileList;
    }

    // 将文件分片，chunk 为seek 处
    public static void splitFile(int chunk, File toSplitFile, List<String> splitFileList) {
        String chunkFilePath = PATH + "split/" +
                toSplitFile.getName().substring(0, toSplitFile.getName().lastIndexOf(".")) + "_" +
                chunk + toSplitFile.getName().substring(toSplitFile.getName().lastIndexOf("."));
        long size = toSplitFile.length();
        System.out.println("chunkFilePath:" + chunkFilePath);
        try (RandomAccessFile input = new RandomAccessFile(toSplitFile, "r");
             RandomAccessFile output = new RandomAccessFile(chunkFilePath, "rw")) {
            byte[] bytes = new byte[1024];

            long lastSize = ((chunk + 1) * CHUNK_SIZE * 1024 * 1024) > toSplitFile.length() ?
                    toSplitFile.length() : ((chunk + 1) * CHUNK_SIZE * 1024 * 1024);
            long currentSize = (chunk) * CHUNK_SIZE * 1024 * 1024;
            System.out.println("currentSize:" + currentSize);
            input.seek(currentSize);
            while ((currentSize < lastSize ) && input.read(bytes) != -1) {
                output.write(bytes);
                currentSize += 1024;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        splitFileList.add(chunkFilePath);
    }

}
