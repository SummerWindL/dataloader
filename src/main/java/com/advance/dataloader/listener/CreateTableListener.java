package com.advance.dataloader.listener;

import com.advance.dataloader.config.DataLoaderConfiguration;
import com.advance.dataloader.sqlloader.ScriptRunner;
import com.advance.dataloader.config.ServiceConfig;
import com.advance.dataloader.config.StartupConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;

/**
 * 服务启动建表监听
 * @author Advance
 * @date 2021年11月19日 15:48
 * @since V1.0.0
 */
@Component
@Slf4j
public class CreateTableListener  extends StartupConfiguration {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private DataLoaderConfiguration dlc;

    @Override
    public void afterStartup(ApplicationContext applicationContext) {
        log.info("服务启动成功，开始启动建表监听...");
        initCreateTable();
    }

    private void initCreateTable() {
        Connection conn = null;
        try {
            conn = ServiceConfig.dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null,
                    dlc.getUserName().toUpperCase() , "DR_SQL_LOADER_LOG", null); //参数schemaPattern: 数据库名称模式匹配，null表示不缩小搜索范围数据库名，对于oracle来说就用户名
            if(rs.next()){
                log.info("DR_SQL_LOADER_LOG（sql执行日志表）表存在...不需要重新创建！");
            } else{
                log.info("表不存在...使用【ScriptRunner】执行建表语句");
                ScriptRunner runner = new ScriptRunner(conn,false,true);
                Resources.setCharset(Charset.forName("UTF8"));
                runner.runScript(Resources.getResourceAsReader("sql/init.sql"));
                log.info("执行sql文件成功......");
            }
        } catch (SQLException | IOException e) {
            try {
                conn.rollback();
                log.error("数据回滚成功");
            } catch (SQLException e1) {
                log.error("数据回滚失败，系统错误");
            }
            log.error("执行sql文件进行数据库创建失败....", e);

        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }

        }
        log.info("-------建表监听结束------");
    }


}
