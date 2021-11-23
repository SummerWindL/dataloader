package com.advance.dataloader.common.util;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author https://blog.csdn.net/chen_2890
 * @description FileUtil
 * @date 2019/6/14 17:29
 */
@Slf4j
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


    /**
     * 递归查询文件夹下以xxx结尾的文件
     * @author Advance
     * @date 2021/11/9 9:23
     * @param file
     * @param fileList
     * @param fileSuffix
     */
    public static void func(File file, List<File> fileList, String fileSuffix) {
        File[] fs = file.listFiles();
        for (File f : fs) {
            //若是目录，则递归打印该目录下的文件
            if (f.isDirectory()){
                log.info("\n 开始扫描：{}",f.getPath());
                func(f, fileList, fileSuffix);
            }
            if (f.isFile()){
                //若是文件，直接打印
                if(f.getName().endsWith(fileSuffix)){
                    fileList.add(f);
                }
            }
        }
    }

    /**
     *
     * 递归统计该目录下的文件个数（只统计文件，如果目录下还有目录一直往下查找）
     * @param directory，这里的参数也可以换成string类型，直接给出文件夹路径
     * @return
     */
    private static  int countFileNumber(File directory){
        int n = 0;
        if (!directory.isDirectory()){
            return 1;
        }
        File[] files = directory.listFiles();
        List<File> result = Arrays.asList(files).stream().filter(l -> l.getName().endsWith("sql")).collect(Collectors.toList());
        for(File direc : files){
            n+=countFileNumber(direc);
        }
        return n;
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

    public static int mount = 0;
    public static void main(String[] args) {
        String filename = "D:\\日常工作\\fudian\\06_脚本文件\\处理脚本\\升级脚本";
        /*//创建一个 File 实例，表示路径名是指定路径参数的文件
        File file = new File(filename);
        args=new String[]{"declare"};//
        for (int i = 0; i < args.length; i++) {
            findFile(file, args[i]);
            print(args[i]);
        }*/

        //System.out.println(countFileNumber(new File(filename)));
        //System.out.println(getAllFiles(filename,".sql").size());

        List<File> allFiles = getAllFiles(filename, ".sql");
        Map<String, Integer> map = new HashMap<>();
        List<File> repeatList = new ArrayList<File>();//用于存放重复的元素的list
        //1:map.containsKey()   检测key是否重复
        for (File s : allFiles) {
            //1:map.containsKey()   检测key是否重复
            if (map.containsKey(s.getName())) {
                File student1 = new File(s.getPath());
                repeatList.add(student1);//
                Integer num = map.get(s.getName());
                map.put(s.getName(), num + 1);
            } else {
                map.put(s.getName(), 1);

            }
        }
        System.out.println(JSONArray.toJSONString(repeatList));



    }
    public static boolean isTrueFile(File file) {
        if(!file.exists() || !file.canRead())
            return false;
        if (file.getName().startsWith("."))
            return false;
        if (file.getName().endsWith("."))
            return false;
        return true;
    }
    public static void findFile(File file, String word) {
        File[] listFiles = file.listFiles();
        //得到一个File数组，它默认是按文件最后修改日期排序的
        for (int i = 0; i < listFiles.length; i++)
        {
            if (listFiles[i].isDirectory())
                findFile(listFiles[i], word);
            else if (isTrueFile(listFiles[i]))
                search(listFiles[i], word);
        }
    }
    public static void search(File file, String word) {
        try
        {
            int j = 0, k = 0, ch = 0;
            String str = null;
            FileReader in = new FileReader(file);
            while ((ch = in.read()) != -1)
            {
                str += (char) ch;
            }
            if (str != null)
            {
                while (str.indexOf(word, j) != -1)
                {
                    k++;
                    j = str.indexOf(word, j) + 1; // 返回第一次出现的指定子字符串在此字符串中的索引
                }
            }
            if (k > 0)
            {
                System.out.println("在" + file.getAbsolutePath() + "有    " + k+ " 个关键字" + word);
                mount++;
            }
            in.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void print(String word) {
        if (mount != 0)
        {
            System.out.println("一共找到    " + mount + " 个文件包含关键字" + word + "! \n");
            mount=0;
        }
        else
        {
            System.out.println("没有找到相应的文件");
        }
    }

}

