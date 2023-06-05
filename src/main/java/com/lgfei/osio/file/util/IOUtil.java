package com.lgfei.osio.file.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public final class IOUtil {

    // 删除文件夹
    public static void removeDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子文件夹
                        removeDir(file.getAbsolutePath());
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
            // 删除空文件夹
            dir.delete();
        }
    }

    public static void downloadFile(String fileUrl, String savePath) throws IOException {
        URL url = new URL(fileUrl);
        BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        inputStream.close();
    }

    public static void cloneRepository(String repositoryUrl, String codeDir, long currTime) {
        try{
            // 清理旧文件
            removeDir(codeDir);
            // 创建目标文件夹
            String clonePath = codeDir + currTime;
            File cloneDir = new File(clonePath);
            cloneDir.mkdirs();

            // 执行 git clone 命令
            ProcessBuilder processBuilder = new ProcessBuilder("git", "clone", repositoryUrl, clonePath);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("代码克隆失败！Exit code: " + exitCode);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String readFileToStr(String filePath) {
        StringBuilder content = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if( null != reader){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }

    public static void writeStrToFile(String content, String filePath){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            if(null != writer){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
