package com.my.client.service.impl;

import com.my.aspact.RpcInjector;
import com.my.aspact.annotations.RpcService;
import com.my.client.service.DemoService;
import com.my.server.service.UserService;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class DemoServiceImpl implements DemoService {

    @RpcService
    private UserService userService;

    public DemoServiceImpl() {
        RpcInjector.inject(this);
    }

    @Override
    public String sayHello(Long id) {
        String userName = userService.getUserName(id);
        return "Hello " + ", " + userName;
    }
}
