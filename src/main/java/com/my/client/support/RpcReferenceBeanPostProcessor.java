package com.my.client.support;

import com.my.aspect.annotations.RpcReference;
import com.my.client.RpcClientProxy;
import com.my.net.Transporter;
import com.my.net.config.RpcConfigProperties;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Component
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private Transporter transporter;

    @Resource
    private RpcConfigProperties rpcConfig;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // 使用 Spring 官方工具获取原始类，彻底解决多层代理问题
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                injectRpcProxy(bean, field);
            }
        }
        return bean;
    }

    private void injectRpcProxy(Object bean, Field field) {
        try {
            String host = rpcConfig.getHost();
            int port = rpcConfig.getServerPort();
            RpcClientProxy proxy = new RpcClientProxy(transporter, host, port);
            Class<?> type = field.getType();
            Object proxyInstance = proxy.getProxy(type);
            // 2. 反射注入
            field.setAccessible(true);
            field.set(bean, proxyInstance);
        } catch (Exception e) {
            throw new RuntimeException("RPC 代理注入失败: " + field.getName(), e);
        }
    }
}
