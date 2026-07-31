package com.my.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class LocalRegister {
    private static final Map<String, String> REGISTER = new ConcurrentHashMap<>();

    public static void register(String interfaceName, String implClassName) {
        REGISTER.put(interfaceName, implClassName);
    }

    public static String discover(String interfaceName) {
        return REGISTER.get(interfaceName);
    }
}
