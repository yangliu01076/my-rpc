package com.my;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@SpringBootApplication(scanBasePackages = {"com.my.server", "com.my.register", "com.my.aspect"
        , "com.my.net", "com.my.request", "com.my.response"})
public class RpcServerApp {
        public static void main(String[] args) {
                SpringApplication springApplication = new SpringApplication(RpcServerApp.class);
                ConfigurableApplicationContext run = springApplication.run(args);
        }
}
