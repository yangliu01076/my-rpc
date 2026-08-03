package com.my.aspect.annotations;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
}
