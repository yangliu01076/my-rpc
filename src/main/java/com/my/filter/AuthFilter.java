package com.my.filter;

import com.my.request.RpcRequest;
import com.my.response.RpcResponse;

/**
 * @author duoyian
 * @since 2026/8/4
 */
public class AuthFilter implements Filter {
    @Override
    public RpcResponse invoke(Invoker invoker, RpcRequest request) {
        System.out.println("【Auth】开始鉴权");
        if (false) {
            RpcResponse res = new RpcResponse();
                 res.setError("鉴权失败");
                 return res;
        }
        return invoker.invoke(request);
    }
}
