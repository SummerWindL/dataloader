package com.advance.dataloader.sqlloader;

import com.advance.dataloader.common.base.AtomicVariable;
import com.advance.dataloader.common.util.UUIDUtil;
import com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog;
import com.advance.dataloader.repo.sqlloader.SqlLoaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Advance
 * @date 2021年11月09日 16:14
 * @since V1.0.0
 */
@Slf4j
public class ScriptRunnerFile {

    private final Connection connection;

    private final boolean runSuccess = true;

    private final boolean stopOnError;
    private final boolean autoCommit;

    private AtomicVariable atomicVariable;

    private String uuid = "";

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private PrintWriter logWriter = null;
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private PrintWriter errorLogWriter = null;
    private SqlLoaderRepository sqlLoaderRepository;

    public SqlLoaderRepository getSqlLoaderRepository() {
        return sqlLoaderRepository;
    }

    public void setSqlLoaderRepository(SqlLoaderRepository sqlLoaderRepository) {
        this.sqlLoaderRepository = sqlLoaderRepository;
    }

    /**
     * Default constructor
     */
    public ScriptRunnerFile(Connection connection, boolean autoCommit,
                        boolean stopOnError,AtomicVariable atomicVariable) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        this.atomicVariable = atomicVariable;
        File logFile = new File("execute_db.log");
        File errorLogFile = new File("execute_db_error.log");
        try {
            if (logFile.exists()) {
                logWriter = new PrintWriter(new FileWriter(logFile, true));
            } else {
                logWriter = new PrintWriter(new FileWriter(logFile, false));
            }
        } catch(IOException e){
            System.err.println("Unable to access or create the db_execute log");
        }
        try {
            if (errorLogFile.exists()) {
                errorLogWriter = new PrintWriter(new FileWriter(errorLogFile, true));
            } else {
                errorLogWriter = new PrintWriter(new FileWriter(errorLogFile, false));
            }
        } catch(IOException e){
            System.err.println("Unable to access or create the db_execute error log");
        }
        String timeStamp = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss").format(new java.util.Date());
        println("\n-------\n" + timeStamp + "\n-------\n");
        printlnError("\n-------\n" + timeStamp + "\n-------\n");
    }

    public void runScript(List<File> files) {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                 runScript(connection, files);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            final String errText = String.format("Error executing  %s",
                    e.getMessage());
            printlnError(errText);
            System.err.println(errText);
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        } finally {
            atomicVariable.setRunSuccess(false);
            /*try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }*/
            flush();
        }

    }

    /**
     * 另外一种写法
     * @author Advance
     * @date 2021/11/10 11:42
     * @param files
     */
    public void runScriptNew(List<File> files) {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            String fileName = "";

            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                for (File file : files){
                    log.info("当前执行文件为：{}",file.getPath());
                    String errText = String.format("Success executing  %s",
                            "当前执行文件为："+file.getPath());
                    fileName = file.getName();
                    printlnError(errText);
                    println(errText);
                    //扫描一遍之前执行过的文件，如果执行过且正常执行完毕，不在执行，如果存在问题重新执行，并且置状态
                    DrSqlLoaderLog sqllog = sqlLoaderRepository.selectBySqlNameAndPath(file.getName(),file.getPath());
                    uuid = UUIDUtil.getUUID();
                    String fileCharset = FileProcess.getFileCharsetByScanner(file.getPath());

                    if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("1")){//执行成功不在执行
                        continue;
                    }else if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("0")){//执行失败继续执行
                        runScript(connection, file); //执行
                    }else{ //未执行
                        DrSqlLoaderLog drSqlLoaderLog = new DrSqlLoaderLog();
                        drSqlLoaderLog.setSerialid(uuid);
                        drSqlLoaderLog.setSqlFileName(file.getName());
                        drSqlLoaderLog.setSqlFilePath(file.getPath());
                        drSqlLoaderLog.setSqlFileCharset(fileCharset);
                        runScript(connection, file); //执行
                        drSqlLoaderLog.setSqlFileExecuteFlag("1");//默认成功
                        sqlLoaderRepository.save(drSqlLoaderLog);
                    }

                }

            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            final String errText = String.format("Error executing  %s",
                    e.getMessage());
            printlnError(errText);

            DrSqlLoaderLog one = sqlLoaderRepository.selectById(uuid);
            one.setSqlFileExecuteFlag("0");
            one.setSqlFileExecuteLog(errText);
            sqlLoaderRepository.save(one);

            System.err.println(errText);
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        } finally {
            //connection.rollback();
            flush();
        }

    }

    /**
     * 单个文件执行sql脚本
     * @author Advance
     * @date 2021/11/10 11:41
     * @param connection
     * @param file
     */
    private void runScript(Connection connection, File file) {
        ScriptRunner runner = new ScriptRunner(connection);
        runner.setLogWriter(null);//设置是否输出日志
        runner.setErrorLogWriter(errorLogWriter);//设置是否输出错误日志
        runner.setAutoCommit(true);//自动提交
        runner.setFullLineDelimiter(false);
        runner.setDelimiter(";");////每条命令间的分隔符
        runner.setSendFullScript(false);
        runner.setStopOnError(false);
        String fileCharset = "";
        try {
            fileCharset = FileProcess.getFileCharsetByScanner(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            runner.runScript(new InputStreamReader(new FileInputStream(file.getPath()), fileCharset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void runScript(Connection connection, List<File> files) {
        ScriptRunner runner = new ScriptRunner(connection);
        runner.setLogWriter(logWriter);//设置是否输出日志
        runner.setErrorLogWriter(errorLogWriter);//设置是否输出错误日志
        runner.setAutoCommit(true);//自动提交
        runner.setFullLineDelimiter(false);
        runner.setDelimiter(";");////每条命令间的分隔符
        runner.setSendFullScript(false);
        runner.setStopOnError(false);
        for(File file : files){
            log.info("当前执行文件为：{}",file.getPath());
            String errText = String.format("Success executing  %s",
                    "当前执行文件为："+file.getPath());
            printlnError(errText);
            println(errText);
            //扫描一遍之前执行过的文件，如果执行过且正常执行完毕，不在执行，如果存在问题重新执行，并且置状态
            String fileCharset = "";
            try {
                fileCharset = FileProcess.getFileCharsetByScanner(file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

            DrSqlLoaderLog sqllog = sqlLoaderRepository.selectBySqlNameAndPath(file.getName(),file.getPath());
            String uuid = UUIDUtil.getUUID();
            try {

                if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("1")){//执行成功不在执行
                    continue;
                }else if(!ObjectUtils.isEmpty(sqllog)&&sqllog.getSqlFileExecuteFlag().equals("0")){//执行失败继续执行
                    runner.runScript(new InputStreamReader(new FileInputStream(file.getPath()), fileCharset));
                }else{ //未执行
                    DrSqlLoaderLog drSqlLoaderLog = new DrSqlLoaderLog();
                    drSqlLoaderLog.setSerialid(uuid);
                    drSqlLoaderLog.setSqlFileName(file.getName());
                    drSqlLoaderLog.setSqlFilePath(file.getPath());
                    drSqlLoaderLog.setSqlFileCharset(fileCharset);
                    drSqlLoaderLog.setSqlFileExecuteFlag("1");//默认成功
                    sqlLoaderRepository.save(drSqlLoaderLog);
                    runner.runScript(new InputStreamReader(new FileInputStream(file.getPath()), fileCharset));
                }
                //connection.setAutoCommit(true);
            }catch (Exception e) { //执行失败
                String errMsg = String.format("Error executing  %s",
                        e.getMessage());
                DrSqlLoaderLog one = sqlLoaderRepository.selectById(uuid);
                one.setSqlFileExecuteFlag("0");
                one.setSqlFileExecuteLog(errMsg);
                sqlLoaderRepository.save(one);
                System.err.println(errText);
            }
        }
        runner.closeConnection();
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
        if (errorLogWriter != null) {
            errorLogWriter.flush();
        }
    }


    /**
     * Setter for logWriter property
     *
     * @param logWriter - the new value of the logWriter property
     */
    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Setter for errorLogWriter property
     *
     * @param errorLogWriter - the new value of the errorLogWriter property
     */
    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    private void println(Object o) {
        if (logWriter != null) {
            logWriter.println(o);
        }
    }

    private void printlnError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
        }
    }
}
