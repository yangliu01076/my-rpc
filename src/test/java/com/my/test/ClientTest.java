package com.my.test;

import com.my.client.service.DemoService;
import com.my.client.service.impl.DemoServiceImpl;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class ClientTest {
    public static void main(String[] args) {
        // 获取动态代理对象，就像调用本地方法一样！
//        RpcClientProxy proxy = new RpcClientProxy(new SocketTransporter(),"127.0.0.1", 8080);
//        RpcClientProxy proxy = new RpcClientProxy(new NettyTransporter(),"127.0.0.1", 8080);
//        UserService userService = proxy.getProxy(UserService.class);
//        String result = userService.getUserName(1001L);
        DemoService demoService = new DemoServiceImpl();
        String result = demoService.sayHello(1001L);
        System.out.println("收到服务端返回结果: " + result);
    }
}
