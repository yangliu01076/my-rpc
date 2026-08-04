package com.my.filter;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @since 2026/8/4
 */
public class MonitorFilter implements Filter {
    @Override
    public RpcResponse invoke(Invoker invoker, RpcRequest request) {
        long start = System.currentTimeMillis();
        System.out.println("【Monitor】开始调用: " + request.getClassName() + "." + request.getMethodName());

        RpcResponse result = invoker.invoke(request); // 调用下一个节点

        long cost = System.currentTimeMillis() - start;
        System.out.println("【Monitor】调用结束，耗时: " + cost + "ms");
        return result;
    }
}
