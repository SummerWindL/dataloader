package com.advance.dataloader.repo.ftp;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Advance
 * @date 2021年10月14日 11:18
 * @since V1.0.0
 */
@Entity(name = "FTP_UPLOAD_INFO")
@Data
public class FtpUploadInfo implements Serializable {

    @Id
    //@Column(name = "FILE_NAME")
    private String fileName;
    //@Column(name = "TABLE_NAME")
    private String tableName;
    //@Column(name ="SEARCH_CONDITION")
    private String serachCondition;
    //@Column(name ="REMARK")
    private String remark;
    //@Column(name ="SYS_CODE")
    private String sysCode;
    //@Column(name ="IS_EFF")
    private String isEff;

    public FtpUploadInfo() {
    }

    public FtpUploadInfo(String fileName, String tableName, String serachCondition, String remark, String sysCode, String isEff) {
        this.fileName = fileName;
        this.tableName = tableName;
        this.serachCondition = serachCondition;
        this.remark = remark;
        this.sysCode = sysCode;
        this.isEff = isEff;
    }
}
