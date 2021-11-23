package com.advance.dataloader.config;

import com.advance.dataloader.common.util.JsonAdaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;

/**
 * @author Advance
 * @date 2021年11月09日 22:12
 * @since V1.0.0
 */
@Configuration
public class ServiceConfig {

    /**
     * spring 一启动就获取到dataSource
     * @author Advance
     * @date 2021/11/19 15:55
     * @param null
     * @return null
     */
    public static DataSource dataSource = null;

    @Bean
    public JsonAdaptor getJsonAdaptor() {
        JsonAdaptor jsonAdaptor = new JsonAdaptor();
        jsonAdaptor.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return jsonAdaptor;
    }

}
