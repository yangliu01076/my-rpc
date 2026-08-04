package com.my.filter;


import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @since 2026/8/4
 */
public class LogFilter implements Filter {
    @Override
    public RpcResponse invoke(Invoker invoker, RpcRequest request) {
        System.out.println("【Log】请求参数: " + java.util.Arrays.toString(request.getParameters()));
        RpcResponse result = invoker.invoke(request);
        System.out.println("【Log】返回结果: " + result.getData());
        return result;
    }
}
