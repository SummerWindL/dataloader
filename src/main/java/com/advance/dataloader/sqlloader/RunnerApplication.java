package com.advance.dataloader.sqlloader;

import com.advance.dataloader.common.base.AtomicVariable;
import com.advance.dataloader.common.util.FileUtil;
import com.advance.dataloader.common.util.UUIDUtil;
import com.advance.dataloader.config.DataLoaderConfiguration;
import com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog;
import com.advance.dataloader.repo.sqlloader.SqlLoaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * @author Advance
 * @date 2021年11月09日 9:58
 * @since V1.0.0
 */
@Slf4j
@Component
public class RunnerApplication implements ApplicationRunner {

    private Connection conn = null;
    @Autowired
    private DataLoaderConfiguration dataLoaderConfiguration;
    @Autowired
    private AtomicVariable atomicVariable;
    @Autowired
    private SqlLoaderRepository sqlLoaderRepository;
    public void runScript(String driver, String sqluser, String sqlpassword, List<File> files) {
        String url= driver;
        String username=sqluser;
        String password=sqlpassword;
        try {
            Class.forName(dataLoaderConfiguration.getDrive()).newInstance();
            conn =DriverManager.getConnection(url, username, password);
            log.info("\n ===========数据库连接成功：{}============",driver);
            //1、statement命令执行sql文件
            //ScriptRunner runner = new ScriptRunner(conn, false, false);
            /*for(File file : files){
                log.info("当前执行文件为：{}",file.getPath());
                //TODO 先判断当前文件编码格式？ 按照不同的编码格式导入到数据库？
                String fileCharset = FileProcess.getFileCharsetByScanner(file.getPath());
                runner.runScript(new InputStreamReader(new FileInputStream(file.getPath()),fileCharset));
            }*/

            //2、ScriptRunner直接执行文件
            ScriptRunnerFile runner = new ScriptRunnerFile(conn,true,false,atomicVariable);
            runner.setSqlLoaderRepository(sqlLoaderRepository);
            //过滤掉已经执行成功的文件
            //查询全部，过滤掉状态为1的记录
            List<DrSqlLoaderLog> runnerData = sqlLoaderRepository.findAll();
            log.info("\n ====={} 文件夹下 存在.sql结尾的文件总数为：{}=====",dataLoaderConfiguration.getFilePath(),files.size());
            //执行成功的list
            List<DrSqlLoaderLog> result1 = runnerData.stream().filter(n -> n.getSqlFileExecuteFlag().equals("1")).collect(Collectors.toList());
            log.info("\n =====已成功执行文件数量为：{}=====",result1.size());
            ListIterator<File> fileListIterator = files.listIterator();
            while(fileListIterator.hasNext()){
                File next = fileListIterator.next();
                result1.forEach(n -> {
                    if(n.getSqlFilePath().equals(next.getPath())){
                        fileListIterator.remove();
                    }
                });
            }
            log.info("\n =====当前需要执行的sql文件数量为：{}=====",files.size());
            runner.runScript(files);

            //3、第三种方式执行
            //executeSql(files);
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to get oracle driver: " + e);
        } catch (SQLException e) {
            System.err.println("Unable to connect to server: " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @author Advance
     * @date 2021/11/10 15:10
     * @param files
     */
    private void executeSql(List<File> files) throws Exception {
        ScriptRunnerDB runner = new ScriptRunnerDB(conn, false, false);
        runner.setSqlLoaderRepository(sqlLoaderRepository);
        String uuid = "";
        for(File file : files){
            String fileCharset = FileProcess.getFileCharsetByScanner(file.getPath());
            //TODO 先判断当前文件编码格式？ 按照不同的编码格式导入到数据库？
            log.info("当前执行文件为：{}",file.getPath());
            //扫描一遍之前执行过的文件，如果执行过且正常执行完毕，不在执行，如果存在问题重新执行，并且置状态
            DrSqlLoaderLog sqllog = sqlLoaderRepository.selectBySqlNameAndPath(file.getName(),file.getPath());
            uuid = UUIDUtil.getUUID();
            if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("1")){//执行成功不在执行
                continue;
            }else if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("0")){//执行失败继续执行
                runner.runScript(file,fileCharset,"rerun",sqllog.getSerialid()); //需要重新执行
            }else{ //未执行
                runner.runScript(file,fileCharset,"newrun",uuid); //新执行的
            }

        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(dataLoaderConfiguration.isRunScript()){
            List<File> fileList = FileUtil.getAllFiles(dataLoaderConfiguration.getFilePath(), ".sql");
            //List<File> fileList = FileUtil.getAllFiles("E:\\测试", ".sql");
            log.info("==========开始执行SQL脚本===========");
            runScript(dataLoaderConfiguration.getUrl(),dataLoaderConfiguration.getUserName(),
                    dataLoaderConfiguration.getPassWord(),fileList);
            log.info("==========结束执行SQL脚本===========");
        }
    }
}
