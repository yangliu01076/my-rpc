package com.my.request;


import lombok.Data;

/**
 * @author duoyian
 * @date 2026/7/31
 */
@Data
public class RpcRequest implements java.io.Serializable {

    private static final long serialVersionUID = 5451920025800611773L;

    private String requestId;

    private String className;     // 接口全限定名

    private String methodName;   // 方法名

    private Class<?>[] parameterTypes; // 参数类型

    private Object[] parameters; // 参数值
}
