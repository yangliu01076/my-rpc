package com.my.server.core;

import com.my.aspect.annotations.RpcService;
import com.my.net.config.RpcConfigProperties;
import com.my.server.RpcServer;
import com.my.server.service.UserService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Component
public class RpcServerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private RpcServer rpcServer;

    @Resource
    private RpcConfigProperties rpcConfig;

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        // 1. 从事件中获取 ApplicationContext，收集所有 @RpcService
        Map<String, Object> beans = Arrays.stream(
                event.getApplicationContext()
                .getBeanNamesForAnnotation(RpcService.class))
                .collect(Collectors.toMap(
                        name -> name,
                        name -> event.getApplicationContext().getBean(name)
                ));
        // 2. 注册服务
        beans.values().forEach(rpcServer::register);

        // 3. 启动 Netty（在新线程中避免阻塞事件总线）
        new Thread(() -> rpcServer.start(rpcConfig.getServerPort()), "rpc-server").start();
    }
}
