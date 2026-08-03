package com.my.aspect.annotations;

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
public @interface RpcReference {
    String host() default "127.0.0.1";
    int port() default 8083;
    String transporter() default "netty"; // netty / socket
}
