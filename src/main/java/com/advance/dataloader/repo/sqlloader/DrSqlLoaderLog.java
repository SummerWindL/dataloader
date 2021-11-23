package com.advance.dataloader.repo.sqlloader;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Advance
 * @date 2021年11月09日 21:53
 * @since V1.0.0
 */
@Entity(name = "DR_SQL_LOADER_LOG")
@Data
public class DrSqlLoaderLog implements Serializable {
    @Id
    private String serialid;

    /**
     * 文件名
     */
    private String sqlFileName;

    /**
     * 文件路径
     */
    private String sqlFilePath;

    /**
     * 文件编码
     */
    private String sqlFileCharset;

    /**
     * 文件执行状态 0-失败 1-成功
     */
    private String sqlFileExecuteFlag;

    /**
     * 文件执行日志
     */
    private String sqlFileExecuteLog;

    public DrSqlLoaderLog() {
    }

    public DrSqlLoaderLog(String serialid, String sqlFileName, String sqlFilePath, String sqlFileCharset, String sqlFileExecuteFlag, String sqlFileExecuteLog) {
        this.serialid = serialid;
        this.sqlFileName = sqlFileName;
        this.sqlFilePath = sqlFilePath;
        this.sqlFileCharset = sqlFileCharset;
        this.sqlFileExecuteFlag = sqlFileExecuteFlag;
        this.sqlFileExecuteLog = sqlFileExecuteLog;
    }
}
