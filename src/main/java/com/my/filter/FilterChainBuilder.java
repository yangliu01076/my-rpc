package com.my.filter;

import com.my.net.handler.RequestHandler;

import java.util.List;

/**
 * @author duoyian
 * @since 2026/8/4
 */
public class FilterChainBuilder {
    public static Invoker buildFilterChain(Invoker realInvoker, List<Filter> filters) {
        // 从最后一个 Filter 开始往前包装
        Invoker invoker = realInvoker;
        for (int i = filters.size() - 1; i >= 0; i--) {
            Filter filter = filters.get(i);
            final Invoker next = invoker;
            final Filter current = filter;

            invoker = request -> current.invoke(next, request);
        }
        return invoker;
    }

    // 创建真正执行业务的 Invoker（反射调用）
    public static Invoker createRealInvoker(RequestHandler requestHandler) {
        // 这里最终调用你的业务逻辑
        return requestHandler::handle;
    }
}
