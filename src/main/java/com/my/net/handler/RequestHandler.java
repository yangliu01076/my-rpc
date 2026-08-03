package com.my.net.handler;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @date 2026/8/2
 */
public interface RequestHandler {
    RpcResponse handle(RpcRequest request);
}
