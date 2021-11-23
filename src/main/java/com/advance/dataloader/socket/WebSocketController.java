package com.advance.dataloader.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Advance
 * @date 2021年11月12日 21:16
 * @since V1.0.0
 */
@RestController
@RequestMapping("/open/socket")
public class WebSocketController {
    @Value("${mySocket.myPwd}")
    public String myPwd;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 手机客户端请求接口
     * @param id    发生异常的设备ID
     * @param pwd   密码（实际开发记得加密）
     * @throws IOException
     */
    @PostMapping(value = "/onReceive")
    public void onReceive(String id,String pwd) throws IOException {
        if(pwd.equals(myPwd)){  //密码校验一致（这里举例，实际开发还要有个密码加密的校验的），则进行群发
            webSocketServer.broadCastInfo(id);
        }
    }

}
