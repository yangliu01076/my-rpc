package com.my.test;

import com.my.net.transporter.NettyTransporter;
import com.my.server.RpcServer;
import com.my.server.service.UserService;
import com.my.server.service.impl.UserServiceImpl;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class ServerTest {

    public static void main(String[] args) {
        // 实现类
        UserService userService = new UserServiceImpl();

//        RpcServer rpcServer = new RpcServer(new SocketTransporter());
        RpcServer rpcServer = new RpcServer(new NettyTransporter());
        rpcServer.register(userService);
        rpcServer.start(8083);
    }
}
