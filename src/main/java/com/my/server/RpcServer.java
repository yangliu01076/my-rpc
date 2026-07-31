package com.my.server;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class RpcServer {
    // 存放接口与实现类的映射 (比如 UserService -> UserServiceImpl)
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

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
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("RPC Server 启动成功，监听端口: " + port);
            while (true) {
                // 阻塞等待连接 (为了简易，这里直接 new Thread，实际要用线程池)
                Socket socket = serverSocket.accept();
                new Thread(() -> handleRequest(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket socket) {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            // 1. 读取请求
            RpcRequest request = (RpcRequest) input.readObject();

            // 2. 反射调用本地实现类
            Object result = invoke(request);

            // 3. 封装响应
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            response.setData(result);

            // 4. 写回结果
            output.writeObject(response);
        } catch (Exception e) {
            e.printStackTrace();
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
