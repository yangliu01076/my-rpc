package com.my.spi.loader;

import com.my.spi.annotation.SPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class ExtensionLoader <T> {
    private static final String SERVICES_DIRECTORY = "META-INF/services/";

    private final Class<?> type;
    private final ConcurrentHashMap<String, T> cachedInstances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        return new ExtensionLoader<>(type);
    }

    /**
     * 加载所有扩展类
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        Map<String, Class<?>> extensionClasses = new HashMap<>();

        String fileName = SERVICES_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls = ClassLoader.getSystemResources(fileName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            // 格式: name=com.xxx.impl.XxxImpl
                            String[] parts = line.split("=");
                            if (parts.length == 2) {
                                String name = parts[0].trim();
                                String className = parts[1].trim();
                                Class<?> clazz = Class.forName(className);
                                extensionClasses.put(name, clazz);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加载扩展点失败: " + type.getName(), e);
        }

        return extensionClasses;
    }

    /**
     * 获取指定名称的扩展实例（带缓存）
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("扩展点名不能为空");
        }

        // 1. 先从缓存拿实例
        T instance = cachedInstances.get(name);
        if (instance != null) {
            return instance;
        }

        // 2. 从缓存拿 Class，没有则加载配置文件
        Class<?> clazz = cachedClasses.get(name);
        if (clazz == null) {
            Map<String, Class<?>> allClasses = loadExtensionClasses();
            cachedClasses.putAll(allClasses);
            clazz = cachedClasses.get(name);
        }

        if (clazz == null) {
            throw new IllegalStateException("找不到扩展点: " + name + " for " + type.getName());
        }

        // 3. 创建实例并缓存
        try {
            instance = (T) clazz.newInstance();
            cachedInstances.put(name, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("创建扩展实例失败: " + name, e);
        }
    }

    /**
     * 获取默认扩展（@SPI 注解指定的）
     */
    public T getDefaultExtension() {
        SPI spi = type.getAnnotation(SPI.class);
        if (spi == null || spi.value().isEmpty()) {
            return null;
        }
        return getExtension(spi.value());
    }
}
