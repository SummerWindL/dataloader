package com.advance.dataloader.repo.ftp;

import com.advance.dataloader.repo.base.CommonCustomRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author: Advance
 * @create: 2021-10-14 11:17
 * @since V1.0.0
 */
public interface FtpUploadInfoRepository extends CommonCustomRepository<FtpUploadInfo,String> {

    /**
     * 查询所有文件上传记录
     * @author Advance
     * @date 2021/10/14 11:31
     * @return java.util.List<java.lang.Object[]>
     */
    @Query(value = "SELECT file_name,table_name,serach_condition ,remark,sys_code,is_eff FROM FTP_UPLOAD_INFO ",
            nativeQuery = true)
    List<Object[]> selectAll();
}
