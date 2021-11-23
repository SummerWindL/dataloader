package com.advance.dataloader.repo.sqlloader;

import com.advance.dataloader.repo.base.CommonCustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author: Advance
 * @create: 2021-11-09 21:42
 * @since V1.0.0
 */
public interface SqlLoaderRepository extends CommonCustomRepository<DrSqlLoaderLog,String> {

    /**
     * 查询所有日志记录
     * @author Advance
     * @date 2021/10/14 11:31
     * @return java.util.List<java.lang.Object[]>
     */
    @Query(value = "SELECT  serialid," +
                            "sql_file_name," +
                            "sql_file_path ," +
                            "sql_file_charset ," +
                            "sql_file_execute_flag," +
                            "sql_file_execute_log " +
                            "FROM DR_SQL_LOADER_LOG ",
                            nativeQuery = true)
    List<Object[]> selectAll();


    /**
     * 根据sql名称查询单条记录
     * @author Advance
     * @date 2021/11/9 21:58
     * @param sqlFileName
     * @return com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog
     */
    @Query(value = "SELECT  serialid," +
            "sql_file_name," +
            "sql_file_path ," +
            "sql_file_charset ," +
            "sql_file_execute_flag," +
            "sql_file_execute_log " +
            "FROM DR_SQL_LOADER_LOG where sql_file_name = :sqlFileName",
            nativeQuery = true)
    DrSqlLoaderLog selectBySqlName(@Param("sqlFileName") String sqlFileName);


    /**
     * 根据文件名和文件地址查询
     * @author Advance
     * @date 2021/11/20 11:20
     * @param sqlFileName
     * @param sqlFilePath
     * @return com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog
     */
    @Query(value = "SELECT  serialid," +
            "sql_file_name," +
            "sql_file_path ," +
            "sql_file_charset ," +
            "sql_file_execute_flag," +
            "sql_file_execute_log " +
            "FROM DR_SQL_LOADER_LOG where sql_file_name = :sqlFileName and sql_file_path = :sqlFilePath",
            nativeQuery = true)
    DrSqlLoaderLog selectBySqlNameAndPath(@Param("sqlFileName") String sqlFileName,
                                          @Param("sqlFilePath") String sqlFilePath);


    /**
     * 根据ID查询单条记录
     * @author Advance
     * @date 2021/11/10 11:34
     * @param serialid
     * @return com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog
     */
    @Query(value = "SELECT  serialid," +
            "sql_file_name," +
            "sql_file_path ," +
            "sql_file_charset ," +
            "sql_file_execute_flag," +
            "sql_file_execute_log " +
            "FROM DR_SQL_LOADER_LOG where serialid = :serialid",
            nativeQuery = true)
    DrSqlLoaderLog selectById(@Param("serialid") String serialid);


}
