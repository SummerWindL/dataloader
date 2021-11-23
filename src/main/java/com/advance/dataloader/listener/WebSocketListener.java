package com.advance.dataloader.listener;

import com.advance.dataloader.config.StartupConfiguration;
import com.platform.websocket.PlatformWebsocketApplication;
import com.platform.websocket.manager.PlatformWebsocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 使用platform-websocket暴露的【PlatformWebsocketManager】实现websocket管道
 * @author Advance
 * @date 2021年11月19日 17:31
 * @since V1.0.0
 */
@Component
@Slf4j
public class WebSocketListener extends StartupConfiguration {

    @Override
    public void afterStartup(ApplicationContext applicationContext) {
        initWebsocket();
    }

    private void initWebsocket() {
        log.info("启动wehsocket监听......");
        //SpringApplication.run(PlatformWebsocketApplication.class);
        PlatformWebsocketManager.sendQueueToDoctor("","","");
    }
}
