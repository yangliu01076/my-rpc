package com.my.server;

import com.my.net.ProtocolHandler;
import com.my.net.Transporter;
import com.my.net.handler.RequestHandler;
import com.my.request.RpcRequest;
import com.my.response.RpcResponse;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class RpcServer {
    // 存放接口与实现类的映射 (比如 UserService -> UserServiceImpl)
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final Transporter transporter;

    public RpcServer(Transporter transporter) {
        this.transporter = transporter;
    }

    // 注册服务
    public void register(Object service) {
        // 获取该类实现的所有接口，注册到 Map 中
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getName(), service);
            System.out.println("注册服务: " + i.getName());
        }
    }

    // 启动网络服务监听
    public void start(int port) {
        // RpcServer 把网络请求委托给 transporter 的处理器
        transporter.start(port, this::handleRequest);
    }

    private RpcResponse handleRequest(RpcRequest request) {
        try {
            Object result = invoke(request);
            return RpcResponse.success(request.getRequestId(), result);
        } catch (Exception e) {
            RpcResponse resp = new RpcResponse();
            resp.setError(e.getMessage());
            return resp;
        }
    }

    // 核心反射逻辑
    private Object invoke(RpcRequest request) throws Exception {
        String className = request.getClassName();
        Object service = serviceMap.get(className);
        if (service == null) {
            throw new RuntimeException("服务未找到: " + className);
        }

        Class<?> clazz = service.getClass();
        Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(service, request.getParameters());
    }
}
