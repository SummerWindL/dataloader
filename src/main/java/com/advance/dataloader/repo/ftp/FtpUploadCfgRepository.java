package com.advance.dataloader.repo.ftp;

import com.advance.dataloader.repo.base.CommonCustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author: Advance
 * @create: 2021-10-14 14:20
 * @since V1.0.0
 */
public interface FtpUploadCfgRepository extends CommonCustomRepository<FtpUploadCfg,String> {

    @Query(value = "select ID,\n" +
            "SYS_CODE,\n" +
            "DB_DRIVER,\n" +
            "DB_URL,\n" +
            "DB_USER_NAME,\n" +
            "DB_PWD,\n" +
            "FTP_IP,\n" +
            "FTP_PORT,\n" +
            "FTP_USER,\n" +
            "FTP_PWD,\n" +
            "LOCAL_DIR,\n" +
            "FTP_DIR,\n" +
            "ENCODE,\n" +
            "IS_HEADER,\n" +
            "SEPARATOR,\n" +
            "SUFFIX,\n" +
            "GEN_DATE_DIR,\n" +
            "IS_EFF,\n" +
            "IS_GEN_END_FILE,\n" +
            "END_FILE_NAME,\n" +
            "LAST_UPLOAD_TIME,\n" +
            "IS_GEN_LAST_CRLF,\n" +
            "FILE_TYPE,\n" +
            "CRON_EXPRESSION from FTP_UPLOAD_CONFIG where sys_code = :sysCode",nativeQuery = true)
    List<Object[]> queryFtpUploadCfgBySysCode(@Param("sysCode") String sysCode);
}
