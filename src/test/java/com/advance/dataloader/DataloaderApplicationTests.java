package com.advance.dataloader;

import com.advance.dataloader.repo.ftp.FtpUploadCfg;
import com.advance.dataloader.repo.ftp.FtpUploadCfgRepository;
import com.advance.dataloader.repo.ftp.FtpUploadInfo;
import com.advance.dataloader.repo.ftp.FtpUploadInfoRepository;
import com.advance.dataloader.common.util.EntityUtils;
import com.advance.dataloader.common.util.UUIDUtil;
import com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog;
import com.advance.dataloader.repo.sqlloader.SqlLoaderRepository;
import com.advance.dataloader.util.FileUtil;
import info.monitorenter.cpdetector.io.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import java.nio.charset.Charset;
import java.sql.Connection;import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataloaderApplication.class)
class DataloaderApplicationTests {

    @Autowired
    private FtpUploadInfoRepository ftpUploadInfoRepository;
    @Autowired
    private FtpUploadCfgRepository ftpUploadCfgRepository;

    @Autowired
    private SqlLoaderRepository sqlLoaderRepository;
    @Test
    void contextLoads() {
    }


    @Test
    void insert(){
        DrSqlLoaderLog drSqlLoaderLog = new DrSqlLoaderLog();
        drSqlLoaderLog.setSerialid(UUIDUtil.getUUID());
        drSqlLoaderLog.setSqlFileName("测试sql文件.sql");
        drSqlLoaderLog.setSqlFilePath("D\\测试sql文件.sql");
        drSqlLoaderLog.setSqlFileExecuteFlag("0");
        drSqlLoaderLog.setSqlFileExecuteLog("Error xxx");
        DrSqlLoaderLog save = sqlLoaderRepository.save(drSqlLoaderLog);
    }

    @Test
    void query(){
        List<DrSqlLoaderLog> all = sqlLoaderRepository.findAll();
        System.out.println(all.get(0).getSqlFileName());
    }

    @Test
    void getOne(){
        DrSqlLoaderLog byId = sqlLoaderRepository.selectById("97b07f9c87cd42158d7e63e199baa06f");
        byId.setSqlFileExecuteFlag("0");
        sqlLoaderRepository.save(byId);
        System.out.printf(byId.getSqlFileName());
    }

    @Test
    void testQuery(){
        List<Object[]> objects = ftpUploadInfoRepository.selectAll();
        List<FtpUploadInfo> ftpUploadInfos = EntityUtils.castEntity(objects, FtpUploadInfo.class, new FtpUploadInfo());
        for(FtpUploadInfo ftp:ftpUploadInfos){
            log.info("查询结果：{}", ftp.getFileName());
        }
        List<Object[]> lcxxpl = ftpUploadCfgRepository.queryFtpUploadCfgBySysCode("lcxxpl");
        List<FtpUploadCfg> ftpUploadCfgs = EntityUtils.castEntity(lcxxpl, FtpUploadCfg.class, new FtpUploadCfg());
        for(FtpUploadCfg model:ftpUploadCfgs){
            log.info("查询结果：{}", model.getDbUrl());
        }
        /*String s = JSONArray.toJSONString(lcxxpl);
        JSONArray json = JSONArray.parseArray(s);
        List<String> a = new ArrayList<>();
        json.forEach(item -> {
            //log.info(item.toString().replace("[","{").replace("]","}"));
            a.add(item.toString().replace("[","{").replace("]","}"));
        });
        System.out.println(a.toString());
        BaseOracleSqlModel model = new BaseOracleSqlModel();
        model.setRetvalue(a.toString());
        FtpUploadCfg[] ftpUploadCfgs = EntityUtils.baseModel2Clz(model, FtpUploadCfg[].class);
        log.info(SerializationDefine.List2Str(lcxxpl));
//        FtpUploadCfg[] ftpUploadCfg = EntityUtils.baseModel2Clz(lcxxpl, FtpUploadCfg[].class);
        List<FtpUploadCfg> ts = (List<FtpUploadCfg>) CollectionUtils.arrayToList(ftpUploadCfgs);
        for(FtpUploadCfg f:ts){
            log.info("====>{}",f.getDbUrl());
        }*/
    }


    private void connmysql(String sqlhost, String sqluser, String sqlpassword,List<File> files) {
        Connection conn;
        String url="jdbc:oracle:thin:@//192.168.90.230:1521/orcl";
        String username=sqluser;
        String password=sqlpassword;
        try {
           /* Configuration configuration = new PropertiesConfiguration(ScriptRunnerExecSql.class.getClassLoader().getResource("")
                    + "META-INF/spring/db.properties");*/
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn =DriverManager.getConnection(url, username, password);
            ScriptRunner runner = new ScriptRunner(conn);
            Resources.setCharset(Charset.forName("GBK")); //设置字符集,不然中文乱码插入错误
            runner.setLogWriter(null);//设置是否输出日志
            //在resouse中新建一个文件夹：然后放入sql文件
            //Reader resourceAsReader = Resources.getResourceAsReader("升级脚本/2.1.2.3版本升级到2.1.3.1版本增量脚本/有压力测试功能才执行/PKG_FLOW_STRESS_TEST.pck");
            //runner.runScript(resourceAsReader);
            runner.setAutoCommit(true);//自动提交
            runner.setFullLineDelimiter(false);
            //runner.setDelimiter(";");////每条命令间的分隔符
            runner.setSendFullScript(false);
            runner.setStopOnError(false);
            for(File file : files){
                log.info("当前执行文件为：{}",file.getPath());
                runner.runScript(new InputStreamReader(new FileInputStream(file.getPath()),"GBK"));
            }
            //runner.runScript(Resources.getResourceAsReader("sql/CC21-01.sql"));
            runner.closeConnection();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void executeScript(){
        connmysql("","fams_1108","fams_1108",null);
    }


    @Test
    void testFile(){
        List<File> fileList = FileUtil.getFileList("D:\\日常工作\\fudian\\06_脚本文件\\升级脚本\\", ".pck");
        //System.out.println(fileList.get(0).getPath());
        //List<File> fileList = FileUtil.getFileList("E:\\opensource\\spring-batch\\dataloader\\src\\main\\resources\\升级脚本", ".sql");
        /*for(File file : fileList){
            connmysql("","fams_1108","fams_1108",file);
        }*/
    }

    @Test
    void testResource() throws IOException {
        Reader resourceAsReader = Resources.getResourceAsReader("D:\\日常工作\\fudian\\06_脚本文件\\升级脚本\\2.1.2.2版本升级到2.1.2.3版本增量脚本\\20200410_产品模板修改_base.sql");
    }

    @Test
    void testFiles(){
        //List<File> fileList = FileUtil.getFileList("E:\\opensource\\spring-batch\\dataloader\\src\\main\\resources\\升级脚本");
        //List<File> files = FileUtil.traverseFolder2("E:\\opensource\\spring-batch\\dataloader\\src\\main\\resources\\升级脚本");
        //List<File> files = FileUtil.getFiles("E:\\opensource\\spring-batch\\dataloader\\src\\main\\resources\\升级脚本",".sql");
        //要遍历的路径
        List<File> fileList = FileUtil.getAllFiles("E:\\opensource\\spring-batch\\dataloader\\src\\main\\resources\\升级脚本", ".sql");
        for(File files: fileList){
            log.info("文件：{}",files.getPath());
        }

    }

    @Test
    void processFiles() throws IOException {
        List<File> fileList = FileUtil.getAllFiles("D:\\日常工作\\fudian\\06_脚本文件\\升级脚本1", ".sql");
        FileOutputStream out;
        try {
            for(File file:fileList){
                FileInputStream input = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int length = 0;
                //循环读取文件内容，输入流中将最多buf.length个字节的数据读入一个buf数组中,返回类型是读取到的字节数。
                //当文件读取到结尾时返回 -1,循环结束。
                String content = "";
                while((length = input.read(buf)) != -1){
                    content = new String(buf,0,length);
                }
                if(content.contains("/**")){
                    log.info("文件：{}，包含'/**'的文件路径：{}",file.getName(),file.getPath());
                    //转换成 --
                    String result = content.replace("/**", "--").replace("**/","--");
                    out = new FileOutputStream(file);
                    out.write(result.getBytes());
                    out.flush();
                }else if(content.contains("///")){
                    log.info("包含'///'的文件：{}",file.getPath());
                }
                //最后记得，关闭流
                input.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * sql脚本执行入库最终方法
     * @author Advance
     * @date 2021/11/8 17:41
     */
    @Test
    void executeSqlFiles(){
        List<File> fileList = FileUtil.getAllFiles("E:\\升级脚本", ".sql");
        /*for(File file : fileList){
            try {
                System.out.println(getFileCharset(file.getPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        connmysql("","fams_1108","fams_1108",fileList);
    }


    /**
     * SQL文件注释最终处理方法
     * @author Advance
     * @date 2021/11/8 17:40
     */
    @Test
    void processFilesNew() throws IOException {
        List<File> fileList = FileUtil.getAllFiles("E:\\测试", ".sql");
        long start = System.currentTimeMillis();
        for(File file:fileList){
            log.info("开始处理文件中带有/**等注释：{}",file.getName());
            delRemark(file.getPath());
        }
        long end = System.currentTimeMillis();
        log.info("执行耗时：{} ms",end-start);

    }

    /**
     * 删除sql文件
     * @author Advance
     * @date 2021/11/8 21:13
     */
    @Test
    void deleteFile(){
        List<File> fileList = FileUtil.getAllFiles("E:\\升级脚本", ".sql");
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
    @Test
    void renameFile(){
        List<File> fileList = FileUtil.getAllFiles("E:\\升级脚本", ".bak");
        for(File file:fileList){
            //System.out.println(file.getPath());
            file.renameTo(new File(file.getPath().substring(0, file.getPath().indexOf(".bak"))));
        }
    }

    @Test
    void test(){
        String s = "20200330_市场机构增加机构类型数据字典修改.sql.bak";
        String substring = s.substring(0, s.indexOf(".bak"));
        System.out.println(substring);
    }

    @Test
    void testRepRemark(){
        String dirName = "D:\\日常工作\\fudian\\06_脚本文件\\升级脚本2\\2.0.1.1版本升级到2.1.0版本增量脚本\\a.2.0.1.1版本升级2.1.0版本脚本\\1.基础升级脚本\\base\\2.base_update_data.sql";
        /******************** 判断当前的路径是文件还是目录 ********************/
        delRemark(dirName);
    }

    /**
     * 测试转换
     * @author Advance
     * @date 2021/11/8 19:47 
     */
    @Test
    void testRepRemarkCharset(){
        String dirName = "D:\\日常工作\\fudian\\06_脚本文件\\测试\\chb_type_mapping_ex表数据.sql";
        delRemark(dirName);
    }

    public static void delRemark(String dirName) {
        try {
            /************* 备份带有注释的源程序文件,并生成空文件 *************/

            File srcFile = new File(dirName);
            File bakFile = new File(dirName + ".bak");
            bakFile.createNewFile();
            /************* 源程序文件中删除注释代码过程在这里处理 ************/
            /*************** 把注释以外的代码复制到新建立的文件之中 **********/
            //BufferedReader br = new BufferedReader(new FileReader(srcFile));
            //判断当前文件编码格式
            BufferedReader br;
            FileInputStream fis = new FileInputStream(srcFile);
            InputStreamReader isr = new InputStreamReader(fis, "GBK");
            FileReader fr = new FileReader(srcFile);
            //if("ISO_8859_1".equals(getFileCharset(dirName)) || "GBK".equals(getFileCharset(dirName))){
                //使用GBK编码读取
              //  br = new BufferedReader(isr);
            //}else{
                br = new BufferedReader(fr);
            //}
            //BufferedWriter bw = new BufferedWriter(new FileWriter(bakFile));
            FileOutputStream fos = new FileOutputStream(bakFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
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
            fr.close();
            br.close();
            bw.close();
            isr.close();
            osw.close();
            fis.close();
            fos.close();
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

}
