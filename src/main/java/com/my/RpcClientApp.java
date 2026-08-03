package com.my;

import com.my.client.service.DemoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@SpringBootApplication(scanBasePackages = {"com.my.client", "com.my.register", "com.my.aspect"
        , "com.my.net", "com.my.request", "com.my.response"})
public class RpcClientApp {
        public static void main(String[] args) {
                SpringApplication springApplication = new SpringApplication(RpcClientApp.class);
                springApplication.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
                ConfigurableApplicationContext run = springApplication.run(args);
                DemoService demoService = run.getBean(DemoService.class);
                String hello = demoService.sayHello(1001L);
                System.out.println(hello);

        }
}
