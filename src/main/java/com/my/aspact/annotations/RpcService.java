package com.my.aspact.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String host() default "127.0.0.1";
    int port() default 8080;
    String transporter() default "netty"; // netty / socket
}
