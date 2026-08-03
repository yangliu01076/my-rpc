package com.my.net;


import com.my.request.RpcRequest;
import com.my.response.RpcResponse;
import org.springframework.lang.Nullable;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public interface ProtocolHandler<T> {
    /**
     * 解析请求
     */
    @Nullable
    RpcRequest handleRequest(T request);

    /**
     * 处理响应
     */
    void handleResponse(T request, RpcResponse response);
}
