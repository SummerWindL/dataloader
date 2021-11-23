package com.advance.dataloader.sqlloader;

import com.advance.dataloader.common.util.FileUtil;
import info.monitorenter.cpdetector.io.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件处理类，处理不合规的sql文件
 * @author Advance
 * @date 2021年11月09日 10:35
 * @since V1.0.0
 */
@Slf4j
public class FileProcess {
    /**
     * 清除文件中 /****  等注释
     * @author Advance
     * @date 2021/11/9 10:37
     * @param dirName
     */
    public static void delRemark(String dirName) {
        try {
            /************* 备份带有注释的源程序文件,并生成空文件 *************/

            File srcFile = new File(dirName);
            File bakFile = new File(dirName + ".bak");
            bakFile.createNewFile();
            /************* 源程序文件中删除注释代码过程在这里处理 ************/
            /*************** 把注释以外的代码复制到新建立的文件之中 **********/
            //判断当前文件编码格式
             BufferedReader br;
            FileInputStream fis = new FileInputStream(srcFile);
            InputStreamReader isr = new InputStreamReader(fis, "GBK");
            FileReader fr = new FileReader(srcFile);
            if("GBK".equals(getFileCharsetByScanner(dirName))){
                //使用GBK编码读取
                br = new BufferedReader(isr);
            }else{
                br = new BufferedReader(fr);
            }
            /*FileOutputStream fos = new FileOutputStream(bakFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);*/
            //BufferedReader br = new BufferedReader(new FileReader(srcFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(bakFile));
            //PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"GBk")));
            Pattern slash_star_start = Pattern.compile("/\\*.*"); // /*.....
            Pattern star_slash = Pattern.compile("^.*\\*/"); // .......*/
            Pattern slash_star_both = Pattern.compile("/\\*.*\\*/"); // /*.....*/
            Pattern slash2 = Pattern.compile("\\s{0,}//[^;+]*$"); // //....
            //Pattern strPattern = Pattern.compile("\".*\"");
            Matcher mat;
            String s;
            boolean note = false;
            while ((s = br.readLine()) != null) {
                // 替换字符串
                //s = s.replaceAll(strPattern.pattern(), "");
                // 处理/*...*/之间的注释
                s = s.replaceAll(slash_star_both.pattern(), "");
                // d处理//后面的注释
                mat = slash2.matcher(s);
                if (mat.find() && note == false) {
                    s = s.substring(0, mat.start());
                    if (s.trim().length() == 0) {
                        bw.newLine();
                        continue;
                    }
                    bw.write(s);
                    bw.newLine();
                    continue;
                }
                // 处理*/之前的注释
                mat = star_slash.matcher(s);
                if (mat.find()) {
                    note = false;
                    s = s.substring(mat.end());
                    if (s.trim().length() == 0) {
                        bw.newLine();
                        continue;
                    }
                }
                if (note == true) {
                    bw.newLine();
                    continue;
                }
                // d处理/*之后的注释
                mat = slash_star_start.matcher(s);
                if (mat.find()) {
                    note = true;
                    s = s.substring(0, mat.start());
                    if (s.trim().length() == 0) {
                        bw.newLine();
                        continue;
                    }
                }
                bw.write(s);
                bw.newLine();
            }
            //关闭所有流

            br.close();
            bw.close();
            /*isr.close();
            osw.close();
            fis.close();
            fos.close();
            fr.close();*/
            /*************** 删除有注释的源文件并把无注释文件名改名 *************/
            //System.gc();//主动调用gc 清除缓存
            srcFile.delete();
            bakFile.renameTo(new File(dirName));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * https://my.oschina.net/773355/blog/3056653
     * <div>
     * 利用第三方开源包cpdetector获取文件编码格式.<br/>
     * --1、cpDetector内置了一些常用的探测实现类,这些探测实现类的实例可以通过add方法加进来,
     *   如:ParsingDetector、 JChardetFacade、ASCIIDetector、UnicodeDetector. <br/>
     * --2、detector按照“谁最先返回非空的探测结果,就以该结果为准”的原则. <br/>
     * --3、cpDetector是基于统计学原理的,不保证完全正确.<br/>
     * </div>
     * @param filePath
     * @return 返回文件编码类型：GBK、UTF-8、UTF-16BE、ISO_8859_1
     * @throws Exception
     */
    public static String getFileCharset(String filePath) throws Exception {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*ParsingDetector可用于检查HTML、XML等文件或字符流的编码,
         * 构造方法中的参数用于指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码测定。
         * 所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以再多加几个探测器，
         * 比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        File file = new File(filePath);
        try {
            //charset = detector.detectCodepage(file.toURI().toURL());
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            charset = detector.detectCodepage(is, 8);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        String charsetName = "GBK";
        if (charset != null) {
            if (charset.name().equals("US-ASCII")) {
                charsetName = "ISO_8859_1";
            } else if (charset.name().startsWith("UTF")) {
                charsetName = charset.name();// 例如:UTF-8,UTF-16BE.
            }
        }
        return charsetName;
    }



    public static String getFileCharsetByScanner(String filePath) throws Exception {
        File file = new File(filePath);
        Scanner scanner = new Scanner(new FileInputStream(file));
        while (scanner.hasNextLine()) {
            String lineStr = scanner.nextLine().toLowerCase();
            String regex = lineStr.replaceAll("[a-z]*[0-9]*_*,*;*'*\\(*\\)*=*/*-*\"*", "");
            String result = regex.replaceAll(" ", "");
            if (result.length() == 0) {
                continue;
            }

            if (result.equals(new String(result.getBytes("GBK"),"GBK"))) {
                return "UTF-8";
            }
        }
        scanner.close();
        return "GBK";
    }

    /**
     * 删除sql文件
     * @author Advance
     * @date 2021/11/8 21:13
     */
    static void deleteFile(String filesPath){
        List<File> fileList = FileUtil.getAllFiles(filesPath, ".sql");
        for(File file:fileList){
            //System.out.println(file.getPath());
            file.delete();//删除所有sql文件
        }
    }

    /**
     * 重命名文件
     * @author Advance
     * @date 2021/11/8 21:14
     */
    static void renameFile(String filesPath){
        List<File> fileList = FileUtil.getAllFiles(filesPath, ".bak");
        for(File file:fileList){
            file.renameTo(new File(file.getPath().substring(0, file.getPath().indexOf(".bak"))));
        }
    }

    public static void main(String[] args) {
        //查询指定文件夹下所有文件文件编码格式
        /*List<File> allFiles = FileUtil.getAllFiles("D:\\日常工作\\fudian\\06_脚本文件\\升级脚本模板\\升级脚本", ".sql");
        for(File file:allFiles){
            try {
                //log.info("文件：{};编码：{}",file.getPath(),getFileCharset(file.getPath()));
                System.out.println(file.getPath()+"；"+"编码："+ getFileCharsetByScanner(file.getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        //1、process 处理sql文件中的注释
        /*List<File> fileList = FileUtil.getAllFiles("D:\\日常工作\\fudian\\06_脚本文件\\处理脚本\\升级脚本", ".sql");
        long start = System.currentTimeMillis();
        for(File file:fileList){
            try {
                log.info("开始处理文件中带有/**等注释：{}，当前文件编码：{}",file.getName(),getFileCharsetByScanner(file.getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            delRemark(file.getPath());
        }
        long end = System.currentTimeMillis();
        log.info("执行耗时：{} ms",end-start);*/
        //2、删除sql文件
        //deleteFile("D:\\日常工作\\fudian\\06_脚本文件\\处理脚本\\升级脚本");
        //3、重命名文件
        renameFile("D:\\日常工作\\fudian\\06_脚本文件\\处理脚本\\升级脚本");
    }

}
