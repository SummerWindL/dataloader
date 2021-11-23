package com.advance.dataloader;

import com.advance.dataloader.listener.CreateTableEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class DataloaderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DataloaderApplication.class, args);
        run.publishEvent(new CreateTableEvent(new Object()));
    }

}
