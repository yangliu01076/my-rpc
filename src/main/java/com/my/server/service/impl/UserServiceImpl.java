package com.my.server.service.impl;

import com.my.server.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public class UserServiceImpl implements UserService {

    private static final Map<Long, String> USER_MAP = new HashMap<>();
    static {
        USER_MAP.put(1001L, "张三");
        USER_MAP.put(1002L, "李四");
        USER_MAP.put(1003L, "王五");
    }
    @Override
    public String getUserName(Long id) {
        return USER_MAP.get(id);
    }
}
