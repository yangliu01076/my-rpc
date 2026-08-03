package com.my.net.transporter;

import com.my.net.Transporter;
import com.my.net.handler.RequestHandler;
import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author duoyian
 * @date 2026/8/2
 */
public class SocketTransporter implements Transporter {
    private static final ExecutorService BUSINESS_EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public void start(int port, RequestHandler handler) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Socket] Server 启动成功，监听端口: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                BUSINESS_EXECUTOR.submit(() -> process(socket, handler));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(Socket socket, RequestHandler handler) {
        try {
            // 服务端先用输入流去读，等客户端先把头文件和数据同步推过来
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            RpcRequest request = (RpcRequest) input.readObject();
            RpcResponse response = handler.handle(request);

            // 关键：真正实现好业务处理，并把业务返回值准备好最后一秒，才创建和发送 output 到 input 的脏数据
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(response);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RpcResponse send(String host, int port, RpcRequest request) {
        try (Socket socket = new Socket(host, port)) {

            // 核心防线：必须先建立读取连接，再把数据发出去
            // 因为客户端发送的ObjectOutputStream会优先编写对象头信息，服务端能够先读到，防止互相等待
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(request);
            output.flush();

            // 建立 System 通讯等待，读取服务端真正传过来的结果
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            return (RpcResponse) input.readObject();

        } catch (Exception e) {
            throw new RuntimeException("发送失败", e);
        }
    }
}
