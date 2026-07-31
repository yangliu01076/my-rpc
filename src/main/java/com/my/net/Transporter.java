package com.my.net;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public interface Transporter {
    /**
     * 启动服务端，绑定端口并监听
     */
    void start(String host, int port, RequestHandler handler);

    /**
     * 客户端发送请求
     */
    RpcResponse send(String host, int port, RpcRequest request);
}
