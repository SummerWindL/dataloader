package com.advance.dataloader.repo.ftp;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Advance
 * @date 2021年10月14日 14:08
 * @since V1.0.0
 */
@Entity(name = "FTP_UPLOAD_CONFIG")
@Data
public class FtpUploadCfg implements Serializable{
    @Id
    private String id;
    private String sysCode;
    private String dbDriver;
    private String dbUrl;
    private String dbUserName;
    private String dbPwd;
    private String ftpIp;
    private BigDecimal ftpPort;
    private String ftpUser;
    private String ftpPwd;
    private String localDir;
    private String ftpDir;
    private String encode;
    private String isHeader;
    private String separator;
    private String suffix;
    private String genDateDir;
    private String isEff;
    private String isGenEndFile;
    private String endFileName;
    private Date lastUploadTime;
    private String isGenLastCrlf;
    private String fileType;
    private String cronExpression;


    public FtpUploadCfg(String id, String sysCode, String dbDriver, String dbUrl, String dbUserName, String dbPwd, String ftpIp, BigDecimal ftpPort, String ftpUser, String ftpPwd, String localDir, String ftpDir, String encode, String isHeader, String separator, String suffix, String genDateDir, String isEff, String isGenEndFile, String endFileName, Date lastUploadTime, String isGenLastCrlf, String fileType, String cronExpression) {
        this.id = id;
        this.sysCode = sysCode;
        this.dbDriver = dbDriver;
        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPwd = dbPwd;
        this.ftpIp = ftpIp;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        this.ftpPwd = ftpPwd;
        this.localDir = localDir;
        this.ftpDir = ftpDir;
        this.encode = encode;
        this.isHeader = isHeader;
        this.separator = separator;
        this.suffix = suffix;
        this.genDateDir = genDateDir;
        this.isEff = isEff;
        this.isGenEndFile = isGenEndFile;
        this.endFileName = endFileName;
        this.lastUploadTime = lastUploadTime;
        this.isGenLastCrlf = isGenLastCrlf;
        this.fileType = fileType;
        this.cronExpression = cronExpression;
    }

    public FtpUploadCfg() {
    }

    public String getId() {
        return id;
    }

    public String getSysCode() {
        return sysCode;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public String getFtpIp() {
        return ftpIp;
    }

    public BigDecimal getFtpPort() {
        return ftpPort;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public String getFtpPwd() {
        return ftpPwd;
    }

    public String getLocalDir() {
        return localDir;
    }

    public String getFtpDir() {
        return ftpDir;
    }

    public String getEncode() {
        return encode;
    }

    public String getIsHeader() {
        return isHeader;
    }

    public String getSeparator() {
        return separator;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getGenDateDir() {
        return genDateDir;
    }

    public String getIsEff() {
        return isEff;
    }

    public String getIsGenEndFile() {
        return isGenEndFile;
    }

    public String getEndFileName() {
        return endFileName;
    }

    public Date getLastUploadTime() {
        return lastUploadTime;
    }

    public String getIsGenLastCrlf() {
        return isGenLastCrlf;
    }

    public String getFileType() {
        return fileType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
    }

    public void setFtpPort(BigDecimal ftpPort) {
        this.ftpPort = ftpPort;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public void setFtpPwd(String ftpPwd) {
        this.ftpPwd = ftpPwd;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    public void setFtpDir(String ftpDir) {
        this.ftpDir = ftpDir;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setIsHeader(String isHeader) {
        this.isHeader = isHeader;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setGenDateDir(String genDateDir) {
        this.genDateDir = genDateDir;
    }

    public void setIsEff(String isEff) {
        this.isEff = isEff;
    }

    public void setIsGenEndFile(String isGenEndFile) {
        this.isGenEndFile = isGenEndFile;
    }

    public void setEndFileName(String endFileName) {
        this.endFileName = endFileName;
    }

    public void setLastUploadTime(Date lastUploadTime) {
        this.lastUploadTime = lastUploadTime;
    }

    public void setIsGenLastCrlf(String isGenLastCrlf) {
        this.isGenLastCrlf = isGenLastCrlf;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
