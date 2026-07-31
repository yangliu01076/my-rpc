package com.my.core.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class ExtensionLoader <T> {
    private static final String DUBBO_DIRECTORY = "META-INF/dubbo/";
    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    // 获取 ExtensionLoader 实例
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) LOADERS.get(type);
        if (loader == null) {
            LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            loader = (ExtensionLoader<T>) LOADERS.get(type);
        }
        return loader;
    }

    // 核心方法：根据名称获取扩展实现
    public T getExtension(String name) {
        Class<?> clazz = cachedClasses.get(name);
        if (clazz == null) {
            // 简易版：直接扫描指定目录的文件 (实际Dubbo会加载所有URL)
            loadFile();
            clazz = cachedClasses.get(name);
            if (clazz == null) {
                throw new RuntimeException("No such extension: " + name);
            }
        }
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    private void loadFile() {
        // 这里简化了文件读取逻辑，假设我们硬编码或者读取了一个本地文件
        // 文件内容格式例如: dubbo=com.example.DubboProtocol
        // 伪代码示例：
//        cachedClasses.put("netty", NettyTransporter.class);
//        cachedClasses.put("socket", SocketTransporter.class);
    }
}
