package com.my.client;

import com.my.net.Transporter;
import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class RpcClientProxy implements InvocationHandler {

    private final Transporter transporter;
    private final String host;
    private final int port;

    public RpcClientProxy(Transporter transporter, String host, int port) {
        this.transporter = transporter;
        this.host = host;
        this.port = port;
    }

    // 暴露给外部获取代理对象的方法 (类似 MyBatis 的 MapperProxy)
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 封装请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        // 2. 发送网络请求并获取结果
        RpcResponse response = null;
        try {
            response = transporter.send(host, port, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (response == null || response.getError() != null) {
            throw new RuntimeException(response != null ? response.getError() : "response is null");
        }
        return response.getData();
    }
}
