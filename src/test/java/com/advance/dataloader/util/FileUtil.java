package com.advance.dataloader.util;

import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author https://blog.csdn.net/chen_2890
 * @description FileUtil
 * @date 2019/6/14 17:29
 */
public class FileUtil {

    /**
     * @description 不使用递归的方法调用
     * @param path 文件夹路径
     * @return java.util.List<java.io.File>
     * @author https://blog.csdn.net/chen_2890
     * @date 2019/6/14 17:34
     * @version V1.0
     */
    public static List<File> traverseFolder1(String path) {
        List<File> fileList = new ArrayList<>();
        int fileNum = 0, folderNum = 0;
        File file = new File(path);
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    System.out.println("文件夹:" + file2.getAbsolutePath());
                    list.add(file2);
                    folderNum++;
                } else {
                    fileList.add(file2);
                    System.out.println("文件:" + file2.getAbsolutePath());
                    fileNum++;
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        list.add(file2);
                        folderNum++;
                    } else {
                        fileList.add(file2);
                        System.out.println("文件:" + file2.getAbsolutePath());
                        fileNum++;
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
        return fileList;
    }
    /**
     * @description 使用递归的方法调用
     * @param path 文件夹路径
     * @return java.util.List<java.io.File>
     * @author https://blog.csdn.net/chen_2890
     * @date 2019/6/14 17:35
     * @version V1.0
     */
    public static List<File> traverseFolder2(String path) {
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder2(file2.getAbsolutePath());
                    } else {
                        fileList.add(file2);
                        System.out.println("文件:" + file2.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return fileList;
    }

    /**
     * @description 使用递归的方法调用，并判断文件名是否以suffix结尾
     * @param path 文件夹路径
     * @return java.util.List<java.io.File>
     * @author https://blog.csdn.net/chen_2890
     * @date 2019/6/14 17:35
     * @version V1.0
     */
    public static List<File> getFileList(String path,String suffix) {
        List<File> fileList = new ArrayList<>();
        File dir = new File(path);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                // 判断是文件还是文件夹
                if (files[i].isDirectory()) {
                    // 获取文件绝对路径
                    getFileList(files[i].getAbsolutePath(),suffix);
                    // 判断文件名是否以.jpg结尾
                } else if (fileName.endsWith(suffix)) {
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return fileList;
    }

    /**
     * @description 使用递归的方法调用，并判断文件名是否以suffix结尾
     * @param path 文件夹路径
     * @return java.util.List<java.io.File>
     * @author https://blog.csdn.net/chen_2890
     * @date 2019/6/14 17:35
     * @version V1.0
     */
    public static List<File> getFileList(String path) {
        List<File> fileList = null;
        if(CollectionUtils.isEmpty(fileList)){
            fileList = new ArrayList<>();
        }
        File dir = new File(path);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                // 判断是文件还是文件夹
                if (files[i].isDirectory()) {
                    // 获取文件绝对路径
                    getFileList(files[i].getAbsolutePath());
                    // 判断文件名是否以.jpg结尾
                } else if (fileName.endsWith(".sql")) {
                    String strFileName = files[i].getAbsolutePath();
                    //System.out.println("---" + strFileName);
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return fileList;
    }

    /**
     *
     * @author Advance
     * @date 2021/11/8 15:20
 * @param filePath
 * @param fileDot
 * @return java.util.List<java.io.File>
     */
    public static List<File> getFiles(String filePath,String fileDot){
        List<File> fileList=new ArrayList<File>();
        File path=new File(filePath);
        File[] files=path.listFiles();
        for (File file:files) {
            if(file.isFile()){
                String fileName=file.getName();
                if(fileName.endsWith(fileDot)){
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }


    public static void func(File file, List<File> fileList, String fileSuffix) {
        File[] fs = file.listFiles();
        for (File f : fs) {
            //若是目录，则递归打印该目录下的文件
            if (f.isDirectory()){
                func(f, fileList, fileSuffix);
            }
            if (f.isFile()){
                //若是文件，直接打印
                if(f.getName().endsWith(fileSuffix)){
                    fileList.add(f);
                    //System.out.println(f.getName());
                }
            }
        }
    }

    /**
     * 获取指定后缀的所有文件
     * @author Advance
     * @date 2021/11/8 15:29
     * @param path
     * @param suffix
     * @return java.util.List<java.io.File>
     */
    public static List<File> getAllFiles(String path,String suffix){
        //获取其file对象,将所有的符合指定后缀的都放入到list里
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        //指定文件的后缀
        FileUtil.func(file, fileList, suffix);
        return fileList;
    }
}

