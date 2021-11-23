package com.advance.dataloader.sqlloader;

import com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog;
import com.advance.dataloader.repo.sqlloader.SqlLoaderRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Advance
 * @date 2021年11月09日 9:50
 * @since V1.0.0
 */
@Slf4j
public class ScriptRunnerDB {
    private static final String DEFAULT_DELIMITER = ";";
    private static final Pattern SOURCE_COMMAND = Pattern.compile("^\\s*SOURCE\\s+(.*?)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * regex to detect delimiter.
     * ignores spaces, allows delimiter in comment, allows an equals-sign
     */
    public static final Pattern delimP = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);

    private final Connection connection;

    private final boolean stopOnError;
    private final boolean autoCommit;

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private PrintWriter logWriter = null;
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private PrintWriter errorLogWriter = null;

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;

    private String userDirectory = System.getProperty("user.dir");

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
    public ScriptRunnerDB(Connection connection, boolean autoCommit,
                          boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
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

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
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

    /**
     * Set the current working directory.  Source commands will be relative to this.
     */
    public void setUserDirectory(String userDirectory) {
        this.userDirectory = userDirectory;
    }


    /**
     * Runs an SQL script (read in using the Reader parameter)
     *
     * @param reader - the source of the script
     */
    public void runScript(File file,String charset,String runFlag,String uuid) throws IOException, SQLException {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                runScript(connection,file, charset,runFlag,uuid);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (IOException | SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in
     *
     * @param conn - the connection to use for the script
     * @param reader - the source of the script
     * @throws SQLException if any SQL errors occur
     * @throws IOException if there is an error reading from the Reader
     */
    private void runScript(Connection conn, File file,String charset,String runFlag,String uuid) throws IOException,
            SQLException {
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(new FileInputStream(file.getPath()), charset));
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                final Matcher delimMatch = delimP.matcher(trimmedLine);
                if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("//")) {
                    // Do nothing
                } else if (delimMatch.matches()) {
                    setDelimiter(delimMatch.group(2), false);
                } else if (trimmedLine.startsWith("--")) {
                    println(trimmedLine);
                } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {
                    command.append(line.substring(0, line
                            .lastIndexOf(getDelimiter())));
                    command.append(" ");
                    this.execCommand(conn, command, lineReader,file,charset,runFlag,uuid);
                    command = null;
                } else {
                    command.append(line);
                    command.append("\n");
                }
            }
            if (command != null) {
                this.execCommand(conn, command, lineReader, file,charset,runFlag,uuid);
            }
            if (!autoCommit) {
                conn.commit();
            }
            //整个文件读取成功
            //成功入库，写日志  直接失败
            DrSqlLoaderLog drSqlLoaderLog = new DrSqlLoaderLog();
            drSqlLoaderLog.setSerialid(uuid);
            drSqlLoaderLog.setSqlFileName(file.getName());
            drSqlLoaderLog.setSqlFilePath(file.getPath());
            drSqlLoaderLog.setSqlFileCharset(charset);
            drSqlLoaderLog.setSqlFileExecuteFlag("1");//默认成功
            sqlLoaderRepository.save(drSqlLoaderLog);
        }
        catch (IOException e) {
            //整个失败
            //失败 记日志
            final String errText = String.format("Error executing '%s': %s", command, e.getMessage());
            DrSqlLoaderLog one = sqlLoaderRepository.selectById(uuid);
            one.setSqlFileExecuteFlag("0");
            one.setSqlFileExecuteLog(errText);
            sqlLoaderRepository.save(one);
            throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
        } finally {
            conn.rollback();
            flush();
        }
    }

    private void execCommand(Connection conn, StringBuffer command,
                             LineNumberReader lineReader,File file,String charset,
                             String runFlag,String uuid) throws IOException, SQLException {

        if (command.length() == 0) {
            return;
        }

        Matcher sourceCommandMatcher = SOURCE_COMMAND.matcher(command);
        if (sourceCommandMatcher.matches()) {
            this.runScriptFile(file,charset,runFlag, uuid);
            return;
        }

        this.execSqlCommand(conn, command, lineReader,file, charset,runFlag,uuid);
    }

    private void runScriptFile(File file,String charset,String runFlag,String uuid) throws IOException, SQLException {
        this.runScript(file,charset,runFlag, uuid);
    }

    /**
     * 每一行单独执行
     * @author Advance
     * @date 2021/11/10 17:33
     * @param conn
     * @param command
     * @param lineReader
     * @param file
     * @param charset
     * @param runFlag
     * @param uuid
     */
    private void execSqlCommand(Connection conn, StringBuffer command,
                                LineNumberReader lineReader,File file,String charset,
                                String runFlag,String uuid) throws SQLException {

        Statement statement = conn.createStatement();

        println(command);

        boolean hasResults = false;
        try {

            hasResults = statement.execute(command.toString());
        } catch (SQLException e) {
            //失败 记日志
            final String errText = String.format("Error executing '%s' (line %d): %s",
                    command, lineReader.getLineNumber(), e.getMessage());

            printlnError(errText);
            System.err.println(errText);
            if (stopOnError) {
                throw new SQLException(errText, e);
            }
        }

        if (autoCommit && !conn.getAutoCommit()) {
            conn.commit();
        }

        ResultSet rs = statement.getResultSet();
        if (hasResults && rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String name = md.getColumnLabel(i);
                print(name + "\t");
            }
            println("");
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String value = rs.getString(i);
                    print(value + "\t");
                }
                println("");
            }
        }

        try {
            statement.close();
        } catch (Exception e) {
            // Ignore to workaround a bug in Jakarta DBCP
        }
    }

    private String getDelimiter() {
        return delimiter;
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")

    private void print(Object o) {
        if (logWriter != null) {
            logWriter.print(o);
        }
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

    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
        if (errorLogWriter != null) {
            errorLogWriter.flush();
        }
    }
}
