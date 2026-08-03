package com.my;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@ComponentScan(
        basePackages = "com.my",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASPECTJ, // 更灵活的 AspectJ 表达式
                pattern = "com.my.server..*"
        )
)
@SpringBootApplication
public class RpcClientApp {
}
