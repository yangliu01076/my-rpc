package com.my.net.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Data
@Component
@ConfigurationProperties(prefix = "rpc")
public class RpcConfigProperties {
    /**
     * 序列化方式，默认 protostuff
     */
    private String serialization = "protostuff";

    /**
     * 服务端端口
     */
    private Integer serverPort = 8083;

    /**
     * 服务端地址
     */
    private String host = "127.0.0.1";

    /**
     * 客户端超时时间（毫秒）
     */
    private Long clientTimeout = 5000L;
}
