package com.my.aspect;

import com.my.client.RpcClientProxy;
import com.my.net.transporter.NettyTransporter;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class Rpc<T> {
    public T getService(Class<T> clazz) {
        RpcClientProxy proxy = new RpcClientProxy(new NettyTransporter(),"127.0.0.1", 8080);
        return proxy.getProxy(clazz);
    }
}
