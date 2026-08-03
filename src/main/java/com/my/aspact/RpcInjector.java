package com.my.aspact;

import com.my.aspact.annotations.RpcService;
import com.my.client.RpcClientProxy;
import com.my.net.Transporter;
import com.my.net.transporter.NettyTransporter;
import com.my.net.transporter.SocketTransporter;

import java.lang.reflect.Field;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class RpcInjector {
    /**
     * 扫描对象字段，注入 RPC 代理
     */
    public static void inject(Object target) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcService.class)) {
                RpcService annotation = field.getAnnotation(RpcService.class);

                // 1. 根据注解选择 Transporter
                Transporter transporter = createTransporter(annotation.transporter());

                // 2. 创建代理
                RpcClientProxy proxy = new RpcClientProxy(
                        transporter,
                        annotation.host(),
                        annotation.port()
                );
                Object proxyInstance = proxy.getProxy(field.getType());

                // 3. 反射注入
                field.setAccessible(true);
                try {
                    field.set(target, proxyInstance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("注入失败: " + field.getName(), e);
                }
            }
        }
    }

    private static Transporter createTransporter(String type) {
        switch (type) {
            case "socket":
                return new SocketTransporter();
            case "netty":
            default:
                return new NettyTransporter();
        }
    }
}
