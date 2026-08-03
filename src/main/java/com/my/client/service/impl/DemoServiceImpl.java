package com.my.client.service.impl;

import com.my.aspect.RpcInjector;
import com.my.aspect.annotations.RpcReference;
import com.my.client.service.DemoService;
import com.my.server.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Service("demoService")
public class DemoServiceImpl implements DemoService {

    @RpcReference
    private UserService userService;

//    public DemoServiceImpl() {
//        RpcInjector.inject(this);
//    }

    @Override
    public String sayHello(Long id) {
        String userName = userService.getUserName(id);
        return "Hello " + ", " + userName;
    }
}
