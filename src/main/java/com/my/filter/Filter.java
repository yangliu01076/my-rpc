package com.my.filter;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @since 2026/8/4
 */
public interface Filter {
    RpcResponse invoke(Invoker invoker, RpcRequest request);
}
